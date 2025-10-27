package com.example.GreenCloset.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nickname",nullable = false,length =10)
    private String nickname;

    @Column(name = "email",unique = true,length = 255)
    private String email;

    @Column(name = "password",length = 255)
    private String password;

    @Column(name = "introduction",nullable = false,length = 255)
    private String introduction;

    @Column(name ="profile_image_url",nullable = false,length = 500)
    private String profileImageUrl;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    //기능
}
