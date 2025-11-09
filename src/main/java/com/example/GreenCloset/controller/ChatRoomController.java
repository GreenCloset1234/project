package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatMessageResponseDto; // [추가]
import com.example.GreenCloset.dto.ChatRoomCreateRequestDto;
import com.example.GreenCloset.dto.ChatRoomResponseDto;
import com.example.GreenCloset.service.ChatMessageService; // [추가]
import com.example.GreenCloset.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService; // [추가]

    /**
     * 1. 채팅방 생성 또는 조회
     */
    @PostMapping
    public ResponseEntity<Long> getOrCreateChatRoom(
            @Valid @RequestBody ChatRoomCreateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        Long roomId = chatRoomService.createOrGetChatRoom(requestDto.getProductId(), user.getUserId());
        return ResponseEntity.ok(roomId);
    }

    /**
     * 2. 내 채팅방 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getMyChatRooms(
            @AuthenticationPrincipal User user
    ) {
        List<ChatRoomResponseDto> responseDtoList = chatRoomService.getMyChatRooms(user);
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * [신규 API] 3. 특정 채팅방의 메시지 내역 조회 (History)
     * (프론트엔드 ChatPage.jsx의 fetchMessages()가 요청하는 API)
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user // (권한 검사를 위해 user 정보 사용)
    ) {
        // ChatMessageService의 기존 메서드 재사용
        List<ChatMessageResponseDto> messages = chatMessageService.getMessagesByRoomId(roomId, user);
        return ResponseEntity.ok(messages);
    }
}