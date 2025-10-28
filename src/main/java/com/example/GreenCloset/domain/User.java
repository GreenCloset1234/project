package com.example.GreenCloset.domain;

import jakarta.persistence.*;   //JPA 어노테이션 쓸라고
import jakarta.validation.constraints.Size; // 유효한지 안한지
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;   //lombok: getter 메서드 자동생성
import lombok.NoArgsConstructor;    //lombok 기본 생성자 자동생성
import java.time.LocalDateTime;

@Entity     //이 클래스는 JPA가 관리하는 엔티티
@Table(name = "Users")  //매핑할 테이블명 암시
@Getter     //lombok    getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)      //JPA가 객체를 만들떄 기본 생성자 필요
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    // --- 닉네임 수정 ---
    @Column(name = "nickname", nullable = false, unique = true, length = 50) //  unique 추가
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이로 입력해주세요.") // 글자 수 제한
    private String nickname;


    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    public void updateUser(String newNickname, String newIntroduction) {
        if (newNickname != null) {
            this.nickname = newNickname;
        }
        if (newIntroduction != null) {
            this.introduction = newIntroduction;
        }
    }

    public void updateProfileImage(String newProfileImageUrl) {
        this.profileImageUrl = newProfileImageUrl;
    }

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }
}
