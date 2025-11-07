package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*; // DTO 임포트
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
    private final S3Service s3Service; // (상품 생성 시 업로드를 위해 필요)

    /**
     * 상품 등록 (수정: '완전한 URL'을 저장)
     */
    @Transactional
    public ProductDetailResponseDto createProduct(ProductCreateRequestDto requestDto, User user, String imageUrl) {
        // (UserController에서 S3Service.uploadFile을 호출하고,
        //  그 '완전한 URL'을 이 imageUrl 파라미터로 전달했다고 가정)

        Product newProduct = Product.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .productImageUrl(imageUrl) // [수정] '완전한 URL'을 DB에 저장
                .user(user)
                .build();

        Product savedProduct = productRepository.save(newProduct);

        // [수정] DTO의 fromEntity 시그니처 변경
        return ProductDetailResponseDto.fromEntity(savedProduct);
    }

    /**
     * 전체 상품 목록 조회 (수정: S3Service 호출 로직 제거)
     */
    public List<ProductListResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                // [수정] DTO의 fromEntity 시그니처 변경 (S3Service 호출 삭제)
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 상품 상세 조회 (수정: S3Service 호출 로직 제거)
     */
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // [수정] DTO의 fromEntity 시그니처 변경 (S3Service 호출 삭제)
        return ProductDetailResponseDto.fromEntity(product);
    }
}