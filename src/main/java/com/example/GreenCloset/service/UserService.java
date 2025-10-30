package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.repository.UserRepository;
// (아래 DTO들은 dto 패키지에 생성해야 오류가 사라집니다)
import com.example.GreenCloset.dto.UserSignupRequestDto;
import com.example.GreenCloset.dto.UserResponseDto;
import com.example.GreenCloset.dto.UserInfoResponseDto;
import com.example.GreenCloset.dto.LoginRequestDto;
import com.example.GreenCloset.dto.PasswordChangeRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // (JWT 토큰 생성을 위한 JwtUtil 등은 추후 jwt 패키지에 구현 후 주입해야 합니다)
    // private final JwtUtil jwtUtil;

    /**
     * 회원가입 (POST /users/signup)
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto requestDto) {

        // 1. 닉네임 중복 검사
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 2. 이메일 중복 검사
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 3. 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 4. User 객체 생성 (Builder 사용)
        User newUser = User.builder()
                .email(requestDto.getEmail())
                .password(hashedPassword)
                .nickname(requestDto.getNickname())
                .build();

        // 5. DB에 저장
        User savedUser = userRepository.save(newUser);

        // 6. API 명세서에 맞는 응답(DTO)으로 변환
        return new UserResponseDto(savedUser.getUserId(), savedUser.getNickname());
    }

    /**
     * 로그인 (POST /auth/login)
     * (주의: 실제 JWT 토큰 생성 로직은 jwtUtil 구현 후 추가해야 함)
     */
    @Transactional(readOnly = true)
    public String login(LoginRequestDto requestDto) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. (추후 구현) JWT 토큰 생성 로직
        // String token = jwtUtil.createToken(user.getEmail());
        // return token;

        // (임시 반환)
        return "로그인 성공 - 토큰 생성 로직 필요";
    }


     // 내 정보 조회 (GET /users/me)

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        // (userId는 Spring Security Context에서 꺼내오는 것이 더 안전합니다)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. DTO로 변환하여 반환
        return new UserInfoResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getIntroduction()
        );
    }

    /**
     * 내 정보 수정 (PATCH /users/me)
     * (주의: 프로필 이미지 파일 업로드 로직은 ProductService를 참고하여 별도 구현 필요)
     */
    @Transactional
    public UserInfoResponseDto updateUserInfo(Long userId, String newNickname, String newIntroduction) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임 중복 검사 (자기 자신은 제외)
        if (newNickname != null && !newNickname.equals(user.getNickname()) && userRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 1. 도메인(User.java)의 수정 메소드 호출
        user.updateUser(newNickname, newIntroduction);

        // 2. DB에 변경 감지(Dirty Checking)로 자동 저장됨 (save() 호출 불필요)

        // 3. 수정된 정보로 DTO 다시 만들어서 반환
        return new UserInfoResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getIntroduction()
        );
    }


     //비밀번호 변경 (PATCH /users/me/password)

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 새 비밀번호 암호화
        String newHashedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // 3. 도메인(User.java)의 수정 메소드 호출
        user.updatePassword(newHashedPassword);
        // (변경 감지로 자동 저장됨)
    }
}