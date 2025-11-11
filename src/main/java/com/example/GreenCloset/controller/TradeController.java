package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService tradeService;

    /**
     * [신규] 거래 완료 API (구매자가 호출)
     */
    @PostMapping("/{productId}/complete")
    public ResponseEntity<Void> completeTrade(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user // (구매자)
    ) {
        tradeService.completeTrade(productId, user);
        return ResponseEntity.ok().build();
    }
}