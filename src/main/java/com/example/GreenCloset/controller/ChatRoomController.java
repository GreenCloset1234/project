package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatRoomCreateRequestDto;
import com.example.GreenCloset.dto.ChatRoomListResponseDto; // [추가]
import com.example.GreenCloset.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List; // [추가]
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats") // (기본 경로)
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    // (ChatMessageService는 WebSocket 컨트롤러가 사용하므로 여기선 불필요)

    /**
     * [신규] 내 채팅방 목록 조회 (GET /api/v1/chats)
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
     * (POST /api/v1/chats)
     */
    @PostMapping
    public ResponseEntity<Map<String, Long>> createOrGetChatRoom(
            @Valid @RequestBody ChatRoomCreateRequestDto requestDto,
            @AuthenticationPrincipal User user // (구매자)
    ) {
        Long roomId = chatRoomService.createOrGetChatRoom(requestDto, user);

        // (프론트엔드가 { roomId: 1 } 형식을 기대할 수 있으므로 Map 사용)
        Map<String, Long> response = new HashMap<>();
        response.put("roomId", roomId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}