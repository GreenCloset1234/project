package com.example.GreenCloset.dto;

import com.example.GreenCloset.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl; // (S3 풀 URL)
    private String introduction;

    private Long tradeCount;     // 교환 횟수
    private Long savedCarbon;    // 아낀 탄소

    /**
     * [수정] UserService의 '4개 인수' 호출에 맞도록 fromEntity 메서드 수정
     */
    public static UserInfoResponseDto fromEntity(User user, String fullImageUrl, Long tradeCount, Long savedCarbon) {
        if (user == null) {
            return null;
        }

        return UserInfoResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(fullImageUrl) // [수정] 4개 인수를 모두 사용
                .introduction(user.getIntroduction())
                .tradeCount(tradeCount)
                .savedCarbon(savedCarbon)
                .build();
    }
}