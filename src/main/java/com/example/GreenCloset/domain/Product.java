package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.*;

// [수정] BaseEntity 상속 추가
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String productImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    // (엔티티가 직접 비즈니스 로직을 갖도록)
    public void updateStatus(ProductStatus newStatus) {
        this.status = newStatus;
    }
}