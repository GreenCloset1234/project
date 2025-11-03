package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.Product;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor    // [수정] @Builder를 위해 추가
@AllArgsConstructor // [수정] @Builder를 위해 추가
@Builder            // [수정] 클래스 레벨로 이동
public class ChatRoomResponseDto {
    private Long roomId;
    private Long productId;
    private Long buyerId;
    private LocalDateTime createdAt;

    // (기존의 @Builder가 붙은 생성자 삭제 -> @AllArgsConstructor로 대체)

    public static ChatRoomResponseDto fromEntity (ChatRoom chatRoom) {
        // (기존 fromEntity 로직은 Null-Safe 처리가 잘 되어있어서 그대로 유지)
        Product product = chatRoom.getProduct();
        User buyer = chatRoom.getBuyer();

        Long productId = (product != null) ? product.getProductId() : null;
        Long buyerId = (buyer != null) ? buyer.getUserId() : null;

        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .productId(productId)
                .buyerId(buyerId)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}