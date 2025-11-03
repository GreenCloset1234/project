package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.ChatRoomCreateRequestDto; // [수정] ChatRequestDto -> ChatRoomCreateRequestDto
import com.example.GreenCloset.dto.ChatRoomResponseDto;
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
    // (ChatMessageService 삭제 - 이 컨트롤러의 역할이 아님)

    /**
     * 1. 채팅방 생성 또는 조회
     */
    @PostMapping
    public ResponseEntity<Long> getOrCreateChatRoom(
            @Valid @RequestBody ChatRoomCreateRequestDto requestDto, // [수정] ChatRoomCreateRequestDto 사용
            @AuthenticationPrincipal User user // (구매자 정보)
    ) {
        // [수정] Long ID 대신 User 객체의 ID를 사용
        Long roomId = chatRoomService.createOrGetChatRoom(requestDto.getProductId(), user.getUserId());
        return ResponseEntity.ok(roomId);
    }

    /**
     * 2. 내 채팅방 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getMyChatRooms(
            @AuthenticationPrincipal User user // [수정] Long ID 대신 User 객체를 주입받음
    ) {
        // [수정] Long ID 대신 User 객체를 서비스로 전달
        List<ChatRoomResponseDto> responseDtoList = chatRoomService.getMyChatRooms(user);
        return ResponseEntity.ok(responseDtoList);
    }
}