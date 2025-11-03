package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.Trade;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.TradeRequestDto;
import com.example.GreenCloset.dto.TradeResponseDto;
// [수정] CustomException import
import com.example.GreenCloset.global.exception.CustomException;
// [수정] ErrorCode import (이 줄이 꼭 필요합니다!)
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.ProductRepository;
import com.example.GreenCloset.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public TradeResponseDto createTrade(TradeRequestDto requestDto, User buyer) {
        Product product = productRepository.findById(requestDto.getProductId())
                // [수정] import가 추가되어 이 부분이 정상 작동합니다.
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // (TODO: 상품이 이미 판매되었는지 확인하는 로직 필요)
        // if (product.getStatus() == ProductStatus.SOLD) { ... }

        if (product.getUser().getUserId().equals(buyer.getUserId())) {
            // [수정] import가 추가되어 이 부분이 정상 작동합니다.
            // (이 ErrorCode가 작동하려면 아래 ErrorCode.java 파일도 수정해야 합니다)
            throw new CustomException(ErrorCode.CANNOT_TRADE_OWN_PRODUCT);
        }

        Trade newTrade = Trade.builder()
                .product(product)
                .buyer(buyer)
                .total(requestDto.getTotal())
                .completedAt(LocalDateTime.now())
                .build();

        // (TODO: 상품 상태를 'SOLD'로 변경하는 로직)
        // product.setStatus(ProductStatus.SOLD);
        // productRepository.save(product);

        Trade savedTrade = tradeRepository.save(newTrade);
        return TradeResponseDto.fromEntity(savedTrade);
    }

    public List<TradeResponseDto> getMyTrades(User user) {
        Long currentUserId = user.getUserId();

        List<Trade> trades = tradeRepository.findByBuyer_UserIdOrProduct_User_UserId(currentUserId, currentUserId);

        return trades.stream()
                .map(TradeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}