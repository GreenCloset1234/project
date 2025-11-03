package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.jwt.JwtUtil; // 1. JwtUtil import
import com.example.GreenCloset.repository.UserRepository;
import com.example.GreenCloset.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // 2. JwtUtil 주입

    /**
     * 회원가입 (POST /users/signup)
     * (회원가입과 동시에 토큰 발급)
     */
    public UserResponseDto signup(UserSignupRequestDto requestDto) {

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

        User newUser = User.builder()
                .email(requestDto.getEmail())
                .password(hashedPassword)
                .nickname(requestDto.getNickname())
                .build();
        User savedUser = userRepository.save(newUser);

        return new UserResponseDto(savedUser.getUserId(), savedUser.getNickname());
    }

    /**
     * 로그인 (POST /auth/login)
     * (로그인 성공 시 JWT 토큰 반환)
     */
    @Transactional(readOnly = true)
    public String login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. (TODO 제거) JwtUtil로 토큰 생성
        return jwtUtil.createToken(user.getEmail());
    }

    /**
     * 내 정보 조회 (GET /users/me)
     */
    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new UserInfoResponseDto(
                user.getUserId(), user.getEmail(), user.getNickname(),
                user.getProfileImageUrl(), user.getIntroduction()
        );
    }

    /**
     * 내 정보 수정 (PATCH /users/me)
     */
    @Transactional
    public UserInfoResponseDto updateUserInfo(Long userId, String newNickname, String newIntroduction, String newProfileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (newNickname != null && !newNickname.equals(user.getNickname()) && userRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        user.updateUser(newNickname, newIntroduction);

        // (파일 업로드 로직(S3Service)이 완료되면 이 부분도 활성화)
        if (newProfileImageUrl != null) {
            user.updateProfileImage(newProfileImageUrl);
        }

        return new UserInfoResponseDto(
                user.getUserId(), user.getEmail(), user.getNickname(),
                user.getProfileImageUrl(), user.getIntroduction()
        );
    }

    /**
     * 비밀번호 변경 (PATCH /users/me/password)
     */
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String newHashedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.updatePassword(newHashedPassword);
    }

    /**
     * (Controller에서 인증된 유저를 찾기 위한 헬퍼 메소드)
     */
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자를 찾을 수 없습니다."));
    }
}