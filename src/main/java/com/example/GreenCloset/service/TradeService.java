package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus;
import com.example.GreenCloset.domain.Trade;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ProductListResponseDto; // [추가]
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.ProductRepository;
import com.example.GreenCloset.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // [추가]
import java.util.stream.Collectors; // [추가]

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;

    /**
     * 거래 완료 처리
     */
    @Transactional
    public void completeTrade(Long productId, User buyer) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        User seller = product.getUser();

        if (seller.getUserId().equals(buyer.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "자신의 상품을 교환할 수 없습니다.");
        }
        if (product.getStatus() == ProductStatus.TRADED) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "이미 교환이 완료된 상품입니다.");
        }

        product.updateStatus(ProductStatus.TRADED);
        seller.incrementTradeCount();

        Trade newTrade = Trade.builder()
                .product(product)
                .buyer(buyer)
                .total(100L) // (10KG = 100 마일리지)
                .build();

        tradeRepository.save(newTrade);
    }

    /**
     * 내 거래 내역 조회
     * (내가 구매자이거나 판매자인 모든 거래의 '상품' 목록을 반환)
     */
    @Transactional(readOnly = true)
    public List<ProductListResponseDto> getMyTrades(User user) {
        Long userId = user.getUserId();

        // 1. 내 모든 거래(구매/판매) 내역 조회
        List<Trade> myTrades = tradeRepository.findByBuyer_UserIdOrProduct_User_UserId(userId, userId);

        // 2. 거래 내역에서 '상품' 정보만 추출
        return myTrades.stream()
                .map(Trade::getProduct) // (Trade -> Product)
                // [수정] ProductListResponseDto.fromEntity(product) 호출로 수정
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}