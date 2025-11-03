package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor; // [수정] import 추가
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor // [수정] @Builder가 필요로 하는 생성자를 추가
@Builder
public class ProductDetailResponseDto {

    private Long productId;
    private Long userId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String productImageUrl;

    /**
     * Product 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static ProductDetailResponseDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        Long userId = null;
        String nickname = null;
        User user = product.getUser();
        if (user != null) {
            userId = user.getUserId();
            nickname = user.getNickname();
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
                .build();
    }
}