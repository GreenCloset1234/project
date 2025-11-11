package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.User;
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
    private LocalDateTime createdAt; // (BaseEntity 상속 필요)

    /**
     * [수정] fromEntity 시그니처 변경 (인수 1개)
     */
    public static ProductDetailResponseDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        User user = product.getUser();
        String nickname = "알 수 없음";
        Long userId = null;

        if (user != null) {
            nickname = user.getNickname();
            userId = user.getUserId();
        }

        return ProductDetailResponseDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .content(product.getContent())
                .productImageUrl(product.getProductImageUrl())
                .nickname(nickname)
                .userId(userId)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt()) // (BaseEntity 상속 필요)
                .build();
    }
}