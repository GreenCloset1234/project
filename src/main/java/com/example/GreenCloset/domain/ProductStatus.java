package com.example.GreenCloset.domain;

import lombok.Getter;

@Getter
public enum ProductStatus {
    AVAILABLE,  // 판매중
    RESERVED,   // 예약중
    SOLD_OUT,   // 거래완료 (판매완료)
    TRADED      // 교환완료 (필요 시)
}