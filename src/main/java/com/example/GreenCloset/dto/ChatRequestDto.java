package com.example.GreenCloset.dto;

import jakarta.validation.constraints.NotBlank;
// [삭제] import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    // [수정] roomId, senderId 필드 제거
    // (이유: Controller가 @DestinationVariable과 Principal로 이미 알고 있음)

    // [수정] 비어있거나 공백만 있는 문자열을 허용하지 않도록 수정
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}