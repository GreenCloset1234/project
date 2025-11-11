package com.example.GreenCloset.domain;

public enum ProductStatus {
    AVAILABLE("교환 가능"), // 0
    RESERVED("예약 중"),   // 1
    TRADED("교환 완료"); // 2

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}