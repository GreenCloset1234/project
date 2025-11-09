package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
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
    // [수정] S3Service 주입 삭제 (ProductController에서만 사용)
    // private final S3Service s3Service;

    /**
     * 상품 등록 (DB에 '완전한 URL' 저장)
     */
    @Transactional
    public ProductDetailResponseDto createProduct(ProductCreateRequestDto requestDto, User user, String imageUrl) {

        Product newProduct = Product.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .productImageUrl(imageUrl) // (Controller가 전달한 '완전한 URL'을 DB에 저장)
                .user(user)
                .build();

        Product savedProduct = productRepository.save(newProduct);

        return ProductDetailResponseDto.fromEntity(savedProduct);
    }

    /**
     * 전체 상품 목록 조회 (DB에 저장된 '완전한 URL' 사용)
     */
    public List<ProductListResponseDto> getAllProducts() {
        return productRepository.findAll() // (TODO: 추후 Paging, Sort 적용)
                .stream()
                .map(ProductListResponseDto::fromEntity) // (DTO가 엔티티의 Full URL을 사용)
                .collect(Collectors.toList());
    }

    /**
     * 상품 상세 조회 (DB에 저장된 '완전한 URL' 사용)
     */
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailResponseDto.fromEntity(product); // (DTO가 엔티티의 Full URL을 사용)
    }

    /**
     * [신규] 특정 사용자 ID로 상품 목록 조회
     */
    public List<ProductListResponseDto> getProductsByUserId(Long userId) {
        // (ProductRepository에 findByUser_UserId 메서드 추가 필요)
        List<Product> products = productRepository.findByUser_UserId(userId);

        return products.stream()
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}