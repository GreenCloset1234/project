package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponseDto {

    private Long productId;
    private String title;
    private String productImageUrl;
    private String nickname;
    private ProductStatus status;

    /**
     * [수정] fromEntity 시그니처 변경 (인수 1개)
     */
    public static ProductListResponseDto fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        User user = product.getUser();
        String nickname = (user != null) ? user.getNickname() : "알 수 없음";

        return ProductListResponseDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .productImageUrl(product.getProductImageUrl())
                .nickname(nickname)
                .status(product.getStatus())
                .build();
    }
}