package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Chat_Rooms")
@Getter
@NoArgsConstructor

public class ChatRoom {
    @Id
    @Column(name = "room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
