package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder; // [수정] @Builder 추가
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder // [수정] Builder 오류 해결
@NoArgsConstructor(access = AccessLevel.PROTECTED) // [수정] JPA를 위한 기본 생성자
@AllArgsConstructor // [수정] @Builder를 위한 모든 필드 생성자
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

    private LocalDateTime completedAt; // (거래 완료 시간)
}