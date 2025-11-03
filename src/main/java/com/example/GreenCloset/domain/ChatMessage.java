package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder // [추가] ChatMessageService의 .builder()를 위해
@NoArgsConstructor(access = AccessLevel.PROTECTED) // [추가] JPA 기본 생성자
@AllArgsConstructor // [추가] @Builder를 위해
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false)
    private String content;

    // (이 필드는 ChatMessageService에서 .now()로 직접 설정하므로 BaseEntity 미사용)
    private LocalDateTime sentAt;
}