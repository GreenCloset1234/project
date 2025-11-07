package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String senderProfileImageUrl; // (S3 풀 URL)
    private String content;
    private LocalDateTime sentAt;

    /**
     * [수정] fromEntity 시그니처 변경 (senderFullProfileUrl 파라미터 제거)
     */
    public static ChatMessageResponseDto fromEntity(ChatMessage message) {
        if (message == null) {
            return null;
        }

        User sender = message.getSender();
        ChatRoom room = message.getChatRoom();

        Long senderId = null;
        String senderName = "알 수 없는 사용자";
        String senderProfileImageUrl = null; // (기본 프로필 URL)

        if (sender != null) {
            senderId = sender.getUserId();
            senderName = sender.getNickname();
            // [수정] 엔티티에서 '완전한 URL'을 직접 가져옴
            senderProfileImageUrl = sender.getProfileImageUrl();
        }

        Long roomId = (room != null) ? room.getRoomId() : null;

        return ChatMessageResponseDto.builder()
                .messageId(message.getMessageId())
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .senderProfileImageUrl(senderProfileImageUrl) // [수정]
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}