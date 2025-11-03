package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.TradeRequestDto;
import com.example.GreenCloset.dto.TradeResponseDto;
import com.example.GreenCloset.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService tradeService;

    /**
     * 1. 거래 생성 (구매 요청)
     */
    @PostMapping
    public ResponseEntity<TradeResponseDto> createTrade(
            @Valid @RequestBody TradeRequestDto requestDto,
            @AuthenticationPrincipal User user // (구매자 정보)
    ) {
        TradeResponseDto responseDto = tradeService.createTrade(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 2. 내 거래 내역 조회
     */
    @GetMapping("/me")
    public ResponseEntity<List<TradeResponseDto>> getMyTrades(
            @AuthenticationPrincipal User user // [수정] @AuthenticationPrincipal 사용
    ) {
        // [수정] Long ID 대신 User 객체를 서비스로 전달 (오류 수정)
        List<TradeResponseDto> responseDtoList = tradeService.getMyTrades(user);
        return ResponseEntity.ok(responseDtoList);
    }
}