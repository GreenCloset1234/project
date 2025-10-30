package com.example.GreenCloset.repository;

import com.example.GreenCloset.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}