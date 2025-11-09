package com.example.GreenCloset.service;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final TradeRepository tradeRepository;

    // ... (signup, loginAndGetUser 메서드는 기존과 동일) ...
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
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
        // [수정] user 객체를 재사용하도록 변경
        return fetchUserInfo(user);
    }

    /**
     * [신규] ID로 특정 사용자 정보 조회
     */
    public UserInfoResponseDto getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return fetchUserInfo(user);
    }

    /**
     * [신규] 닉네임 / 한줄소개 수정
     */
    @Transactional
    public UserInfoResponseDto updateMyInfo(User user, UserUpdateRequestDto requestDto) {
        // (참고: 닉네임 중복 검사가 필요하다면 여기에 로직 추가)

        user.updateProfile(requestDto.getNickname(), requestDto.getIntroduction());
        User savedUser = userRepository.save(user);
        return fetchUserInfo(savedUser);
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
        String oldFileKey = user.getProfileImageUrl(); // (DB에는 Full URL이 저장되어 있음)
        String newFileUrl = s3Service.uploadProfileImage(imageFile); // (Full URL 반환)

        user.updateProfileImage(newFileUrl); // (새 Full URL을 DB에 저장)
        userRepository.save(user);

        if (oldFileKey != null) {
            s3Service.deleteFile(oldFileKey); // (Full URL을 전달하여 삭제)
        }

        // [수정] URL 중복 버그 수정
        // s3Service.getFullFileUrl(newFileUrl) -> newFileUrl
        // (이유: newFileUrl은 이미 S3Service가 반환한 "완전한 URL"이므로)
        return newFileUrl;
    }


    /**
     * [신규] UserInfoResponseDto를 만드는 공통 로직 추출
     */
    private UserInfoResponseDto fetchUserInfo(User user) {
        // (DB에 Full URL이 저장되어 있으므로 getFullFileUrl 호출 불필요)
        String fullImageUrl = user.getProfileImageUrl();
        Long tradeCount = tradeRepository.countByBuyer_UserIdOrProduct_User_UserId(user.getUserId(), user.getUserId());
        Long savedCarbon = tradeCount * 10;
        return UserInfoResponseDto.fromEntity(user, fullImageUrl, tradeCount, savedCarbon);
    }
}