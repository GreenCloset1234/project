package com.example.GreenCloset.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 닉네임, 한줄소개 변경 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {

    // (참고: @NotBlank 대신 @Size를 사용하면 null은 허용하되, 길이는 제한할 수 있습니다.)
    // (만약 null을 허용하지 않으려면 @NotBlank를 사용하세요)

    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    @Size(max = 100, message = "한줄소개는 100자 이하로 입력해주세요.")
    private String introduction;
}