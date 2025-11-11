package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat; // [추가]
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime; // [추가]

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

    // [신규] 'Invalid Date' 오류 해결을 위해 추가
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * [수정] fromEntity 시그니처 변경 (인수 1개) 및 createdAt 필드 추가
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
                .createdAt(product.getCreatedAt()) // [신규] (BaseEntity 필요)
                .build();
    }
}