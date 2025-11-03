package com.example.GreenCloset.controller;

import com.example.GreenCloset.dto.TradeRequestDto;
import com.example.GreenCloset.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades") // 거래 공통 경로
public class TradeController {

    private final TradeService tradeService;

    /**
     * 거래 완료 (POST /trades)
     */
    @PostMapping
    public ResponseEntity<String> createTrade(@Valid @RequestBody TradeRequestDto requestDto) {
        // (TODO: 임시 ID. 추후 Spring Security에서 실제 *구매자* ID를 가져와야 함)
        Long currentBuyerId = 2L; // (1번 유저(판매자)와 달라야 함)

        // (ERD에 total이 추가되었으므로, Service/DTO 수정이 필요할 수 있습니다)
        tradeService.createTrade(requestDto.getProductId(), currentBuyerId, 0L);

        return ResponseEntity.status(HttpStatus.CREATED).body("거래가 성공적으로 완료되었습니다.");
    }
}