package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String profileImageUrl;

    private String introduction;

    @Column(columnDefinition = "BIGINT default 0")
    private Long tradeCount; // [신규] 거래 횟수

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 프로필 이미지 변경
     */
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * [신규] 닉네임, 한줄소개 변경
     */
    public void updateProfile(String nickname, String introduction) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (introduction != null) {
            this.introduction = introduction;
        }
    }

    /**
     * [신규] 거래 횟수 1 증가
     */
    public void incrementTradeCount() {
        this.tradeCount = (this.tradeCount == null ? 0L : this.tradeCount) + 1;
    }
}