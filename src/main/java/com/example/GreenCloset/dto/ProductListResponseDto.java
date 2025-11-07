package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
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
    private String productImageUrl; // (S3 풀 URL)
    private String nickname;

    /**
     * [수정] fromEntity 시그니처 변경 (fullImageUrl 파라미터 제거)
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
                // [수정] 엔티티에서 '완전한 URL'을 직접 가져옴
                .productImageUrl(product.getProductImageUrl())
                .nickname(nickname)
                .build();
    }
}