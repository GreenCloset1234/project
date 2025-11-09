package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // [추가]

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * [신규] 특정 사용자가 작성한 상품 목록 조회
     */
    List<Product> findByUser_UserId(Long userId);
}