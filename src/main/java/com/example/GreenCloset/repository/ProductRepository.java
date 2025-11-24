package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.ProductStatus; // [추가]
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * [기존] 특정 사용자가 작성한 상품 목록 조회 (findByUser_UserId)
     */
    List<Product> findByUser_UserId(Long userId);

    /**
     * [신규 추가]
     * 지정된 상태 목록(예: AVAILABLE, RESERVED)에 포함된 상품들만 조회
     * (메인 전체 목록 조회 시 사용)
     */
    List<Product> findByStatusIn(List<ProductStatus> statuses);

    /**
     * [신규 추가]
     * 특정 사용자가 작성한 상품 중, 지정된 상태 목록에 포함된 상품들만 조회
     * (다른 사용자 프로필 조회 시 사용)
     */
    List<Product> findByUser_UserIdAndStatusIn(Long userId, List<ProductStatus> statuses);
}