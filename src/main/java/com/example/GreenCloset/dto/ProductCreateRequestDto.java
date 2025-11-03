package com.example.GreenCloset.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateRequestDto {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    // 참고: productImageUrl은 MultipartFile로 따로 받으므로 DTO에 포함되지 않는다.
    // userId는 컨트롤러에서 인증 정보 (e.g. @AuthenticationPrincipal)를 통해 받아서 서비스로 넘겨주므로 DTO에 포함되지 않는다.
}