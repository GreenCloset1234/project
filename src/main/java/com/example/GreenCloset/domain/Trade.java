package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // (completed_at 자동 생성을 위해)
@Builder
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;

    // (ERD 기반)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true) // 하나의 상품은 하나의 거래만
    private Product product;

    // (ERD 기반 - 구매자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @CreatedDate // (거래 완료 시점)
    @Column(updatable = false)
    private LocalDateTime completedAt;

    // (ERD 기반 - 마일리지 적립용)
    @Column(nullable = false)
    private Long total;
}