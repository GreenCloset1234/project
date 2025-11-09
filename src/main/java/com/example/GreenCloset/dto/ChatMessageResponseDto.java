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
    private String senderProfileImageUrl;
    private String content;
    private LocalDateTime sentAt;

    /**
     * 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static ChatMessageResponseDto fromEntity(ChatMessage message) {
        if (message == null) {
            return null;
        }

        User sender = message.getSender();
        ChatRoom room = message.getChatRoom();

        Long senderId = null;
        String senderName = "알 수 없는 사용자";
        String senderProfileImageUrl = null; // (기본 프로필 URL 등)

        if (sender != null) {
            senderId = sender.getUserId();
            senderName = sender.getNickname();
            senderProfileImageUrl = sender.getProfileImageUrl(); // 엔티티의 URL 필드
        }

        Long roomId = (room != null) ? room.getRoomId() : null;

        return ChatMessageResponseDto.builder()
                .messageId(message.getMessageId())
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .senderProfileImageUrl(senderProfileImageUrl)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}