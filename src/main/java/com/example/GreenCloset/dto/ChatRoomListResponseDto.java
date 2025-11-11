package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomListResponseDto {

    private Long roomId;
    private Long productId;
    private String productImageUrl;
    private String lastMessage;

    // 상대방 정보
    private Long partnerId;
    private String partnerNickname;
    private String partnerProfileImg;

    @Builder
    public ChatRoomListResponseDto(Long roomId, Long productId, String productImageUrl,
                                   String lastMessage, Long partnerId,
                                   String partnerNickname, String partnerProfileImg) {
        this.roomId = roomId;
        this.productId = productId;
        this.productImageUrl = productImageUrl;
        this.lastMessage = lastMessage;
        this.partnerId = partnerId;
        this.partnerNickname = partnerNickname;
        this.partnerProfileImg = partnerProfileImg;
    }

    /**
     * Entity를 DTO로 변환 (상대방 정보와 마지막 메시지를 주입받음)
     */
    public static ChatRoomListResponseDto fromEntity(ChatRoom chatRoom, User partner, String lastMessage) {
        return ChatRoomListResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .productId(chatRoom.getProduct().getProductId())
                .productImageUrl(chatRoom.getProduct().getProductImageUrl())
                .lastMessage(lastMessage)
                .partnerId(partner.getUserId())
                .partnerNickname(partner.getNickname())
                .partnerProfileImg(partner.getProfileImageUrl())
                .build();
    }
}