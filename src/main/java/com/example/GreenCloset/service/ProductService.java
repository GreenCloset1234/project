package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*;
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductDetailResponseDto createProduct(ProductCreateRequestDto requestDto, User user, String imageUrl) {
        Product newProduct = Product.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .productImageUrl(imageUrl)
                .user(user)
                .status(ProductStatus.AVAILABLE)
                .build();

        Product savedProduct = productRepository.save(newProduct);
        return ProductDetailResponseDto.fromEntity(savedProduct);
    }

    /**
     * 2. 전체 상품 목록 조회 (수정됨)
     * - '교환 완료(TRADED)' 상태의 상품은 제외하고 조회합니다.
     */
    public List<ProductListResponseDto> getAllProducts() {
        // [수정] '교환 가능'과 '예약 중'인 상품만 조회
        List<ProductStatus> statusesToShow = List.of(ProductStatus.AVAILABLE, ProductStatus.RESERVED);

        // [수정] findAll() 대신 findByStatusIn() 사용
        // Repository에 findByStatusIn 메서드가 필요합니다. (아래 Repository 코드 참고)
        List<Product> products = productRepository.findByStatusIn(statusesToShow);

        return products.stream()
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponseDto.fromEntity(product);
    }

    /**
     * 4. 특정 사용자가 작성한 상품 목록 조회 (수정됨)
     * - '교환 완료(TRADED)' 상태의 상품은 제외합니다.
     * - (참고: 마이페이지 조회 시 영향이 있을 수 있습니다. 아래 ⚠️ 참고)
     */
    public List<ProductListResponseDto> getProductsByUserId(Long userId) {
        // [수정] '교환 가능'과 '예약 중'인 상품만 조회
        List<ProductStatus> statusesToShow = List.of(ProductStatus.AVAILABLE, ProductStatus.RESERVED);

        // [수정] findByUser_UserId() 대신 findByUser_UserIdAndStatusIn() 사용
        // Repository에 findByUser_UserIdAndStatusIn 메서드가 필요합니다.
        List<Product> products = productRepository.findByUser_UserIdAndStatusIn(userId, statusesToShow);

        return products.stream()
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * [수정됨] 상품 거래 상태 변경 (예약 중 / 거래 완료)
     */
    @Transactional
    public void updateProductStatus(Long productId, ProductStatus newStatus, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 1. 권한 체크 (상품 주인인지)
        if (!product.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "상품 상태를 변경할 권한이 없습니다.");
        }

        // 2. [중요] 상태 변경 제한 로직 삭제됨
        // (기존 파일에 있던 TRADED 변경 불가 로직을 제거하여, 버튼 클릭 시 정상 동작하게 함)

        product.updateStatus(newStatus);
    }
}