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

    /**
     * [신규] 메시지 저장 (Service가 DTO 변환까지 책임짐)
     * (이 메서드가 없어서 DB 저장(문제2)과 방송(문제1)이 모두 실패했습니다)
     */
    @Transactional
    public ChatMessageResponseDto saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage newMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .sentAt(LocalDateTime.now()) // (BaseEntity가 없다면)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(newMessage);

        // DTO로 변환하여 컨트롤러에 반환
        return ChatMessageResponseDto.fromEntity(savedMessage);
    }

    /**
     * 특정 채팅방의 메시지 내역 조회
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

        // DB에서 메시지 목록 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_RoomIdOrderBySentAtAsc(roomId);

        // DTO 리스트로 변환
        return messages.stream()
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}