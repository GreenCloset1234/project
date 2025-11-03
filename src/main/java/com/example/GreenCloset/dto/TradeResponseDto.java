package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.Trade;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor; // [수정] import 추가
import lombok.Builder;         // [수정] import 추가
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor // [수정] @Builder를 위해 추가
@Builder            // [수정] @Builder 추가
public class TradeResponseDto {

    private Long tradeId;
    private Long total;
    private LocalDateTime completedAt;

    // 상품 정보
    private Long productId;
    private String productImageUrl;

    // 구매자 정보
    private Long buyerId;

    // 판매자 정보
    private Long sellerId; // (기존 userId를 sellerId로 명확하게 변경)
    private String sellerNickname; // (기존 nickname을 sellerNickname으로 명확하게 변경)

    /**
     * Trade 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * (기존의 생성자 로직을 정적 메서드로 변경)
     */
    public static TradeResponseDto fromEntity(Trade trade) {
        if (trade == null) {
            return null;
        }

        Long buyerId = null;
        User buyer = trade.getBuyer();
        if (buyer != null) {
            buyerId = buyer.getUserId();
        }

        Long productId = null;
        String productImageUrl = null;
        Long sellerId = null;
        String sellerNickname = null;

        Product product = trade.getProduct();
        if (product != null) {
            productId = product.getProductId();
            productImageUrl = product.getProductImageUrl();

            User seller = product.getUser();
            if (seller != null) {
                sellerId = seller.getUserId();
                sellerNickname = seller.getNickname();
            }
        }

        return TradeResponseDto.builder()
                .tradeId(trade.getTradeId())
                .total(trade.getTotal())
                .completedAt(trade.getCompletedAt())
                .buyerId(buyerId)
                .productId(productId)
                .productImageUrl(productImageUrl)
                .sellerId(sellerId)
                .sellerNickname(sellerNickname)
                .build();
    }
}