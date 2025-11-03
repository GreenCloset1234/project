package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder // [추가] TradeService의 .builder()를 위해
@NoArgsConstructor(access = AccessLevel.PROTECTED) // [추가] JPA 기본 생성자
@AllArgsConstructor // [추가] @Builder를 위해
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer; // (구매자)

    private Long total; // (거래 금액)

    // (이 필드는 TradeService에서 .now()로 직접 설정하므로 BaseEntity 미사용)
    private LocalDateTime completedAt;
}