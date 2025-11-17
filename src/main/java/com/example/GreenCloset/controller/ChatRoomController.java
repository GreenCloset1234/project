package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
// import com.example.GreenCloset.dto.ChatMessageDto; // [삭제]
import com.example.GreenCloset.dto.ChatRoomCreateRequestDto;
import com.example.GreenCloset.dto.ChatRoomListResponseDto;
import com.example.GreenCloset.service.ChatRoomService;
// import com.example.GreenCloset.service.ChatService; // [삭제]
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats") // 기본 경로
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    // [삭제] private final ChatService chatService;

    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponseDto>> getMyChatRooms(
            @AuthenticationPrincipal User user
    ) {
        List<ChatRoomListResponseDto> myChatRooms = chatRoomService.getMyChatRooms(user);
        return ResponseEntity.ok(myChatRooms);
    }

    /**
     * 채팅방 생성 (또는 기존 채팅방 ID 반환)
     */
    @PostMapping
    public ResponseEntity<Map<String, Long>> createOrGetChatRoom(
            @Valid @RequestBody ChatRoomCreateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        Long roomId = chatRoomService.createOrGetChatRoom(requestDto, user);
        Map<String, Long> response = new HashMap<>();
        response.put("roomId", roomId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // [삭제] 중복되는 getRoomMessages 메서드 제거
    /*
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getRoomMessages(
            @PathVariable Long roomId
    ) {
        List<ChatMessageDto> history = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(history);
    }
    */
}