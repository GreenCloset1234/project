package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.Trade;
import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.repository.ProductRepository;
import com.example.GreenCloset.repository.TradeRepository;
import com.example.GreenCloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 거래 완료 (POST /trades)
     */
    public void createTrade(Long productId, Long buyerId, Long totalAmount) {

        // 1. 엔티티 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("구매자를 찾을 수 없습니다."));

        // 2. (방어 로직) 본인 상품은 거래할 수 없음
        if (product.getUser().getUserId().equals(buyerId)) {
            throw new IllegalArgumentException("자신의 상품은 거래할 수 없습니다.");
        }

        // 3. (방어 로직) 이미 거래된 상품인지 확인 (ERD의 UNIQUE 제약조건 활용)
        // (이 기능을 위해 TradeRepository 수정이 필요합니다 -> 아래 "다음 단계" 참고)
        if (tradeRepository.existsByProduct(product)) {
            throw new IllegalArgumentException("이미 거래가 완료된 상품입니다.");
        }

        // 4. 거래 생성 및 저장
        Trade trade = Trade.builder()
                .product(product)
                .buyer(buyer)
                .total(totalAmount) // ERD에 추가하신 total 필드
                .build();

        tradeRepository.save(trade);
    }

    /**
     * 나의 거래 내역 조회 (GET /users/me/trades)
     */
    @Transactional(readOnly = true)
    public List<Trade> getMyTrades(Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // (이 기능을 위해 TradeRepository 수정이 필요합니다 -> 아래 "다음 단계" 참고)
        return tradeRepository.findAllByBuyer(buyer);
    }
}