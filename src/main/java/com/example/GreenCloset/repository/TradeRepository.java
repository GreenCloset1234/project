package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // [추가]

public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     *  내 거래 내역 (구매/판매) 조회 시 사용
     * (내가 구매자(buyer)이거나, 내가 상품의 판매자(product.user)인 모든 거래를 조회)
     */
    List<Trade> findByBuyer_UserIdOrProduct_User_UserId(Long buyerId, Long sellerId);

    /**
     *  내 거래 내역 (구매/판매) 횟수 조회
     */
    long countByBuyer_UserIdOrProduct_User_UserId(Long buyerId, Long sellerId);
}