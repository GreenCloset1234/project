package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.*; // domain 임포트
import com.example.GreenCloset.dto.ChatMessageResponseDto;
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.*; // repository 임포트
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
    // [수정] S3Service 주입 제거 (더 이상 필요 없음)
    // private final S3Service s3Service;

    /**
     * 메시지 저장 (수정: S3Service 호출 로직 제거)
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
                .sentAt(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(newMessage);

        // [수정] DTO의 fromEntity 시그니처 변경 (S3Service 호출 삭제)
        return ChatMessageResponseDto.fromEntity(savedMessage);
    }

    /**
     * 특정 채팅방의 메시지 내역 조회 (수정: S3Service 호출 로직 제거)
     */
    public List<ChatMessageResponseDto> getMessagesByRoomId(Long roomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        // (권한 검사 로직 ...)
        Long currentUserId = user.getUserId();
        Long buyerId = chatRoom.getBuyer().getUserId();
        Long sellerId = chatRoom.getProduct().getUser().getUserId();
        if (!currentUserId.equals(buyerId) && !currentUserId.equals(sellerId)) {
            // (ErrorCode.CHATROOM_NOT_AUTHORIZED 등)
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_RoomIdOrderBySentAtAsc(roomId);

        return messages.stream()
                // [수정] DTO의 fromEntity 시그니처 변경 (S3Service 호출 삭제)
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}