package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatMessage;
import com.example.GreenCloset.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private Long messageId;
    private Long senderId;

    // [가이드 2-3]
    private String senderNickname; // (v2 명세서의 'senderName' 역할)

    // [가이드 2-3]
    private String senderProfileImageUrl;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    /**
     * [문제 2 해결]
     * fromEntity가 가이드의 요구사항을 모두 포함하도록 수정
     */
    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        User sender = chatMessage.getSender(); // 보낸 사람(User) 정보

        // [!!] Null-Safe 처리 [!!]
        // sender가 null일 경우를 대비하여 기본값을 설정합니다.
        Long senderId = null;
        String senderNickname = "알 수 없음"; // 또는 "탈퇴한 사용자"
        String senderProfileImageUrl = null; // 기본 프로필 이미지 URL을 넣어도 좋습니다.

        // sender가 null이 아닐 때만 실제 값을 할당합니다.
        if (sender != null) {
            senderId = sender.getUserId();
            senderNickname = sender.getNickname();
            senderProfileImageUrl = sender.getProfileImageUrl();
        }

        return ChatMessageResponseDto.builder()
                // .messageId(chatMessage.getMessageId()) // [기존]
                .messageId(chatMessage.getId())           // [수정] (엔티티의 필드명은 id)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .senderProfileImageUrl(senderProfileImageUrl)
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}