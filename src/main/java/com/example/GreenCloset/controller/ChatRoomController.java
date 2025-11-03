package com.example.GreenCloset.controller;

import com.example.GreenCloset.dto.ChatRequestDto;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.service.ChatMessageService;
import com.example.GreenCloset.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats") //채팅공통경로
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    //채팅방 생성/조회

    @PostMapping
    public ResponseEntity<Long> getOrCreateChatRoom(@Valid @RequestBody ChatRequestDto requestDto) {
        // (TODO: 임시 ID. 추후 Spring Security에서 실제 *구매자* ID를 가져와야 함)
        Long currentBuyerId = 2L;

        Long roomId = chatRoomService.createOrGetChatRoom(requestDto.getProductId(), currentBuyerId);
        return ResponseEntity.ok(roomId);
    }

    /**
     * 내 채팅방 목록 조회 (GET /chats)
     */
    @GetMapping
    public ResponseEntity<List<?>> getMyChatRooms() {
        // (TODO: 임시 ID. 추후 Spring Security에서 실제 유저 ID를 가져와야 함)
        Long currentUserId = 1L;

        // (Service에서 DTO로 변환하는 로직을 권장합니다)
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(currentUserId));
    }
}
