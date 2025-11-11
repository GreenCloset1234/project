package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.*;
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // ... (saveMessage 메서드) ...

    /**
     * [가이드 2] 특정 채팅방의 메시지 내역 조회
     */
    public List<ChatMessageResponseDto> getMessagesByRoomId(Long roomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        // (권한 검사 로직)
        Long currentUserId = user.getUserId();
        Long buyerId = chatRoom.getBuyer().getUserId();
        Long sellerId = chatRoom.getProduct().getUser().getUserId();
        if (!currentUserId.equals(buyerId) && !currentUserId.equals(sellerId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // [가이드 2. 로직 1] DB에서 메시지 목록 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_RoomIdOrderBySentAtAsc(roomId);

        // [가이드 2. 로직 2] DTO 리스트로 변환
        return messages.stream()
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}