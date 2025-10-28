package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Chat_Messages")
@Getter
@NoArgsConstructor

public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "sender_id")
    private Long SenderId;

    @Column(name = "content",length = 500)
    private String content;

    @Column(name = "sent_at",nullable = false)
    private LocalDateTime sentAt;

}


sender_id   room_id    sender_id    content sent_at