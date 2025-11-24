package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.ChatRoom;
import com.example.GreenCloset.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomListResponseDto {

    private Long roomId;
    private Long productId;
    private String productImageUrl;
    private String lastMessage;

    // [수정] 프론트엔드 편의를 위해 상대방 정보를 객체로 묶음
    private PartnerInfo partner;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PartnerInfo {
        private Long id;
        private String nickname;
        private String profileImageUrl;
    }

    /**
     * Entity -> DTO 변환
     */
    public static ChatRoomListResponseDto fromEntity(ChatRoom chatRoom, User partner, String lastMessage) {
        return ChatRoomListResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .productId(chatRoom.getProduct().getProductId())
                .productImageUrl(chatRoom.getProduct().getProductImageUrl())
                .lastMessage(lastMessage)
                .partner(PartnerInfo.builder()
                        .id(partner.getUserId())
                        .nickname(partner.getNickname())
                        .profileImageUrl(partner.getProfileImageUrl())
                        .build())
                .build();
    }
}