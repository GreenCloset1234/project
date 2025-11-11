package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStatusUpdateRequestDto {

    // "AVAILABLE", "RESERVED", "TRADED" 중 하나
    @NotNull(message = "상태를 입력해주세요.")
    private ProductStatus status;
}