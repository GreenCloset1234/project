package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponseDto {

    private Long productId;
    private String title;
    private String content;
    private String productImageUrl;
    private String nickname;
    private Long userId;
    private ProductStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // [신규] 판매자 프로필 이미지 (이 필드가 누락되었습니다)
    private String sellerProfileImg;

    /**
     * [수정] fromEntity 시그니처 변경 (인수 1개)
     */
    public static ProductDetailResponseDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        User user = product.getUser(); // 판매자(User) 정보
        String nickname = "알 수 없음";
        Long userId = null;
        String sellerProfileImg = null; // [신규]

        if (user != null) {
            nickname = user.getNickname();
            userId = user.getUserId();
            sellerProfileImg = user.getProfileImageUrl(); // [신규] User의 프로필 URL 가져오기
        }

        return ProductDetailResponseDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .content(product.getContent())
                .productImageUrl(product.getProductImageUrl())
                .nickname(nickname)
                .userId(userId)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .sellerProfileImg(sellerProfileImg) // [신규] 응답에 포함
                .build();
    }
}