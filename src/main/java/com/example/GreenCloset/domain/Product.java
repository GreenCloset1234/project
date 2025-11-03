package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder // [추가] ProductService의 .builder()를 위해
@NoArgsConstructor(access = AccessLevel.PROTECTED) // [추가] JPA 기본 생성자
@AllArgsConstructor // [추가] @Builder를 위해
public class Product extends BaseEntity { // [수정] createdAt, updatedAt 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String productImageUrl;

    // (TODO: Enum 타입의 ProductStatus(상품 상태) 필드 추가 권장)
    // @Enumerated(EnumType.STRING)
    // private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // (판매자)
    private User user;
}