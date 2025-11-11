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
                .status(ProductStatus.AVAILABLE) // 생성 시 기본 상태 'AVAILABLE'
                .build();

        Product savedProduct = productRepository.save(newProduct);

        // [수정] fromEntity 호출 인수 1개로 변경
        return ProductDetailResponseDto.fromEntity(savedProduct);
    }

    public List<ProductListResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                // [수정] fromEntity 호출 인수 1개로 변경 (메서드 레퍼런스)
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // [수정] fromEntity 호출 인수 1개로 변경
        return ProductDetailResponseDto.fromEntity(product);
    }

    public List<ProductListResponseDto> getProductsByUserId(Long userId) {
        List<Product> products = productRepository.findByUser_UserId(userId);
        return products.stream()
                // [수정] fromEntity 호출 인수 1개로 변경 (메서드 레퍼런스)
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 상품 거래 상태 변경 (예약 중 등)
     */
    @Transactional
    public void updateProductStatus(Long productId, ProductStatus newStatus, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // [권한 체크] 상품 등록자만 상태를 변경할 수 있음
        if (!product.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // (권한 없음 ErrorCode로 수정)
        }

        // '교환 완료' 상태는 TradeService에서만 변경 가능하도록
        if (newStatus == ProductStatus.TRADED || product.getStatus() == ProductStatus.TRADED) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "거래 완료 상태는 변경할 수 없습니다.");
        }

        product.updateStatus(newStatus);
    }
}