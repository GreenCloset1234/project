package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.UserInfoResponseDto;
import com.example.GreenCloset.dto.LoginRequestDto;
import com.example.GreenCloset.dto.UserResponseDto;
import com.example.GreenCloset.dto.UserSignupRequestDto;
import com.example.GreenCloset.dto.PasswordChangeRequestDto;
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.UserRepository;
import com.example.GreenCloset.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // (SecurityConfig에 Bean이 등록되어 있어야 함)

    /**
     * 회원 가입
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            // [수정] import가 추가되어 정상 작동
            throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
        }

        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        User newUser = User.builder()
                .email(requestDto.getEmail())
                .password(encryptedPassword)
                .nickname(requestDto.getNickname())
                .build();
        User savedUser = userRepository.save(newUser);
        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * 로그인
     * (이 메서드는 UserController에서 loginAndGetUser로 호출되거나 login이 User를 반환해야 함)
     */
    /*@Transactional
    public User loginAndGetUser(LoginRequestDto requestDto) { // (메서드 이름을 loginAndGetUser로 가정)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword()
                    )
            );
        } catch (Exception e) {
            // [수정] import가 추가되어 정상 작동
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        return userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }*/
    /**
     * 로그인 (수정: User 객체 반환)
     */
    @Transactional(readOnly = true)
    public User loginAndGetUser(LoginRequestDto requestDto) { // [수정] 메서드 이름, 반환 타입
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 인증된 User 객체 반환
        return user;
    }

    /**
     * 내 정보 조회
     */
    public UserInfoResponseDto getUserInfo(User user) {
        return UserInfoResponseDto.fromEntity(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(PasswordChangeRequestDto requestDto, User user) {
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            // [수정] import가 추가되어 정상 작동
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordCheck())) {
            // [수정] import가 추가되어 정상 작동
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // (혹은 다른 적절한 ErrorCode)
        }

        String newEncryptedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        // [수정] user.setPassword(..) -> user.updatePassword(..) 호출
        // (이 코드가 작동하려면 domain/User.java 파일에 public void updatePassword(String newPassword) { ... } 메서드가 필요합니다)
        user.updatePassword(newEncryptedPassword);
        userRepository.save(user);
    }
}