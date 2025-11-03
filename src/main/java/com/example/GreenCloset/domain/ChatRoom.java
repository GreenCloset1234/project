package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder // [추가] ChatRoomService의 .builder()를 위해
@NoArgsConstructor(access = AccessLevel.PROTECTED) // [추가] JPA 기본 생성자
@AllArgsConstructor // [추가] @Builder를 위해
public class ChatRoom extends BaseEntity { // [수정] createdAt 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer; // (구매자)
}