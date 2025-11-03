package com.example.GreenCloset.dto;

import jakarta.validation.constraints.NotNull; // [수정] import 추가
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeRequestDto {

    // [수정] null 값을 허용하지 않도록 수정
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "거래 금액은 필수입니다.")
    private Long total;
}