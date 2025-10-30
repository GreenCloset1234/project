package com.example.GreenCloset.repository;


import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.Trade;
import com.example.GreenCloset.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    // TradeService가 사용
    boolean existsByProduct(Product product);
    List<Trade> findAllByBuyer(User buyer);
}