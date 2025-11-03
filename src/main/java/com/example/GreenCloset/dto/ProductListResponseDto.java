package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor; // [수정] import 추가
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor // [수정] @Builder가 필요로 하는 생성자를 추가
@Builder
public class ProductListResponseDto {

    private Long productId;
    private String title;
    private String productImageUrl;
    private String nickname;

    /**
     * Product 엔티티를 DTO로 변환하는 정적 팩토리 메서드
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
                .build();
    }
}

