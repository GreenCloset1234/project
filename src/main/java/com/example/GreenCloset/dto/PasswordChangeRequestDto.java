package com.example.GreenCloset.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeRequestDto {

    @NotBlank(message = "현재 비밀번호는 필수 입력 값입니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
    private String newPassword;

    // [수정] 새 비밀번호 확인 필드를 추가했습니다.
    @NotBlank(message = "새 비밀번호 확인은 필수 입력 값입니다.")
    private String newPasswordCheck;
}
