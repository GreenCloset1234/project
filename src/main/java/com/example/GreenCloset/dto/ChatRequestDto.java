package com.example.GreenCloset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    // [수정] null 값을 허용하지 않도록 수정
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long roomId;

    @NotNull(message = "발신자 ID는 필수입니다.")
    private Long senderId;

    // [수정] 비어있거나 공백만 있는 문자열을 허용하지 않도록 수정
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}