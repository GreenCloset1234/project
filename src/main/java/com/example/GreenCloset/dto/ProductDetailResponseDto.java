package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponseDto {

    private Long productId;
    private Long userId; // 판매자 ID
    private String nickname; // 판매자 닉네임
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String productImageUrl; // 상품 이미지 (Full URL)

    /**
     * [수정] 판매자 프로필 이미지 URL 필드 추가
     */
    private String sellerProfileImageUrl; // 판매자 프로필 (Full URL)


    /**
     * Product 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static ProductDetailResponseDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        Long userId = null;
        String nickname = null;
        String sellerProfileImageUrl = null; // [수정]

        User user = product.getUser();
        if (user != null) {
            userId = user.getUserId();
            nickname = user.getNickname();
            sellerProfileImageUrl = user.getProfileImageUrl(); // [수정] (DB에 Full URL이 저장되어 있음)
        }

        return ProductDetailResponseDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .content(product.getContent())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .productImageUrl(product.getProductImageUrl())
                .userId(userId)
                .nickname(nickname)
                .sellerProfileImageUrl(sellerProfileImageUrl) // [수정]
                .build();
    }
}