package com.example.GreenCloset.domain;

import com.example.GreenCloset.dto.ChatMessageDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    // ChatRoom 엔티티를 직접 참조 (FK: room_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    // User 엔티티를 직접 참조 (FK: sender_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 메시지 내용

    @Column(name = "sent_at")
    public LocalDateTime sentAt; // BaseEntity의 createdAt 대신 직접 관리

    @Builder
    public ChatMessage(ChatRoom chatRoom, User sender, String content, LocalDateTime sentAt) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }
}