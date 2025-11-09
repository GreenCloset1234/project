package com.example.GreenCloset.controller;

import com.example.GreenCloset.domain.User;
import com.example.GreenCloset.dto.*;
import com.example.GreenCloset.jwt.JwtUtil;
import com.example.GreenCloset.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // ... (signup, login, handleIllegalArgumentException은 기존과 동일) ...
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserResponseDto responseDto = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto requestDto) {
        User authenticatedUser = userService.loginAndGetUser(requestDto);
        String token = jwtUtil.createToken(authenticatedUser.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // ★ 500 오류 해결을 위한 핵심 코드 ★
    // ★ 이 코드를 UserController 클래스 맨 아래에 추가하세요. ★
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        // Service에서 "비밀번호가 일치하지 않습니다" 등의 예외가 터지면
        // 500 오류 대신, 400 Bad Request와 함께 예외 메시지를 Body에 담아 보냅니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal User user) {
        UserInfoResponseDto responseDto = userService.getUserInfo(user);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        userService.changePassword(requestDto, user);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PutMapping(path = "/me/image", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> updateProfileImage(
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal User user
    ) throws IOException {
        String newImageUrl = userService.updateProfileImage(user, image);
        return ResponseEntity.ok(Map.of("profileImageUrl", newImageUrl));
    }


    /**
     * [신규] 닉네임 / 한줄소개 수정 (인증 필요)
     */
    @PutMapping("/me")
    public ResponseEntity<UserInfoResponseDto> updateMyInfo(
            @Valid @RequestBody UserUpdateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        // (참고: 닉네임 중복 검사가 필요하다면 UserService에서 로직 추가)
        UserInfoResponseDto responseDto = userService.updateMyInfo(user, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * [신규] 다른 사용자 프로필 조회 (인증 불필요)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(
            @PathVariable Long userId
    ) {
        UserInfoResponseDto responseDto = userService.getUserInfoById(userId);
        return ResponseEntity.ok(responseDto);
    }
}