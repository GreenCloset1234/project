package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ProductCreateRequestDto;
import com.example.GreenCloset.dto.ProductDetailResponseDto;
import com.example.GreenCloset.dto.ProductListResponseDto;
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

    // ★★★ [추가] S3 기본 URL 상수 정의 (여기에 실제 버킷 이름을 입력하세요!) ★★★
    // AWS S3 콘솔에서 확인한 사용자님의 S3 버킷 이름으로 교체해야 합니다.
    private final String S3_BASE_URL = "https://greencloset-bucket.s3.ap-northeast-2.amazonaws.com/";

    // 이미지가 없을 때 프론트엔드가 요구하는 대체 URL (No Image 오류 해결)
    private final String DEFAULT_IMAGE_URL = "https://placehold.co/300x300?text=No+Image";


    @Transactional
    public ProductDetailResponseDto createProduct(ProductCreateRequestDto requestDto, User user, String productImageUrl) {
        Product newProduct = Product.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .productImageUrl(productImageUrl)
                .user(user)
                .build();

        Product savedProduct = productRepository.save(newProduct);

        // 생성 직후 반환 시에도 URL 변환 로직이 필요합니다.
        return mapToProductDetailDto(savedProduct);
    }

    public List<ProductListResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                // ★★★ DTO 변환 시 URL 로직을 사용하도록 수정 ★★★
                .map(this::mapToProductListDto)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // ★★★ 상세 조회 시 DTO 변환에 URL 로직을 사용하도록 수정 ★★★
        return mapToProductDetailDto(product);
    }

    // =======================================================
    // ★★★ [추가] 이미지 URL 변환 및 DTO 생성 로직 ★★★
    // =======================================================

    // DTO 변환 헬퍼 (목록 조회용)
    private ProductListResponseDto mapToProductListDto(Product product) {
        String s3Key = product.getProductImageUrl();
        String fullImageUrl = getFullImageUrl(s3Key);

        User user = product.getUser();
        String nickname = (user != null) ? user.getNickname() : "알 수 없음";

        return ProductListResponseDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .productImageUrl(fullImageUrl) // HTTPS URL 전달
                .nickname(nickname)
                .build();
    }

    // DTO 변환 헬퍼 (상세 조회용)
    private ProductDetailResponseDto mapToProductDetailDto(Product product) {
        String s3Key = product.getProductImageUrl();
        String fullImageUrl = getFullImageUrl(s3Key);

        User user = product.getUser();
        Long userId = (user != null) ? user.getUserId() : null;
        String nickname = (user != null) ? user.getNickname() : "알 수 없음";

        return ProductDetailResponseDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .content(product.getContent())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .productImageUrl(fullImageUrl) // HTTPS URL 전달
                .userId(userId)
                .nickname(nickname)
                .build();
    }

    // S3 키를 완전한 Public URL로 변환하는 핵심 로직
    private String getFullImageUrl(String s3Key) {

        // 1. "s3://" 접두사 제거 (DB에 s3://temp-image-url.jpg 와 같이 저장되어 있다면)
        if (s3Key != null && s3Key.startsWith("s3://")) {
            s3Key = s3Key.substring(5);
        }

        if (s3Key != null && !s3Key.isEmpty() && !s3Key.startsWith("http")) {
            // 2. 키(Key)만 남아있다면, BASE_URL과 결합하여 완전한 URL 생성
            return S3_BASE_URL + s3Key;
        } else if (s3Key != null && s3Key.startsWith("http")) {
            // 3. 이미 완전한 URL인 경우 (만약을 대비)
            return s3Key;
        } else {
            // 4. 이미지가 없는 경우 (null)
            return DEFAULT_IMAGE_URL;
        }
    }
}