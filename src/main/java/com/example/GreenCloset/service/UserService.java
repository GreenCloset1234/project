package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*; // DTO 임포트
import com.example.GreenCloset.global.exception.CustomException;
import com.example.GreenCloset.global.exception.ErrorCode;
import com.example.GreenCloset.repository.UserRepository;
import com.example.GreenCloset.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

// (사용되지 않는 AuthenticationManager 관련 import 삭제)

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final TradeRepository tradeRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
        }

        // [수정] 이 줄이 'encryptedPassword' 오류의 해결책입니다.
        // User.builder() 보다 *먼저* 정의되어야 합니다.
        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());

        User newUser = User.builder()
                .email(requestDto.getEmail())
                .password(encryptedPassword) // [수정] 정의된 변수 사용
                .nickname(requestDto.getNickname())
                .build();

        User savedUser = userRepository.save(newUser);
        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public User loginAndGetUser(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    /**
     * 내 정보 조회
     */
    public UserInfoResponseDto getUserInfo(User user) {
        String fullImageUrl = s3Service.getFullFileUrl(user.getProfileImageUrl());
        Long tradeCount = tradeRepository.countByBuyer_UserIdOrProduct_User_UserId(user.getUserId(), user.getUserId());
        Long savedCarbon = tradeCount * 10;
        return UserInfoResponseDto.fromEntity(user, fullImageUrl, tradeCount, savedCarbon);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(PasswordChangeRequestDto requestDto, User user) {
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }
        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordCheck())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        String newEncryptedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.updatePassword(newEncryptedPassword);
        userRepository.save(user);
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public String updateProfileImage(User user, MultipartFile imageFile) throws IOException {
        String oldFileKey = user.getProfileImageUrl();
        String newFileKey = s3Service.uploadProfileImage(imageFile);
        user.updateProfileImage(newFileKey);
        userRepository.save(user);

        if (oldFileKey != null) {
            s3Service.deleteFile(oldFileKey);
        }
        return s3Service.getFullFileUrl(newFileKey);
    }
}