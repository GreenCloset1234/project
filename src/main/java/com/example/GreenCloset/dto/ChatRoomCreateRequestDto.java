package com.example.GreenCloset.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 생성을 요청할 때 사용하는 DTO
 * (ChatRequestDto는 메시지 전송용이므로 분리)
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomCreateRequestDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    // (참고: 구매자 ID는 DTO로 받지 않고,
    //  컨트롤러에서 @AuthenticationPrincipal 등을 통해 인증된 사용자 정보를 가져와서 사용합니다)
}