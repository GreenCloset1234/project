package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Products")
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id" )
    private Long productId;

    @Column(name = "title",nullable = false,length = 255)
    private String title;

    @Column(name = "contnent",nullable = false,length = 255)
    private String contnent;

    @Column(name = "product_image_url")
    private String productImageUrl;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",nullable = false,updatable = false)
    private LocalDateTime updatedAt;
}
