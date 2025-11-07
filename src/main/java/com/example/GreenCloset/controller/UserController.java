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
import org.springframework.web.multipart.MultipartFile; // [추가]

import java.io.IOException; // [추가]
import java.util.Map; // [추가]

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // ... (signup, login, handleIllegalArgumentException, getMyInfo, changePassword 메서드는 동일) ...
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
        // (이 코드가 작동하려면 UserInfoResponseDto.java 수정이 필요)
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

    /**
     * [신규] 프로필 이미지 변경 (인증 필요)
     * (multipart/form-data 형식으로 image 파일만 받음)
     */
    @PutMapping(path = "/me/image", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> updateProfileImage(
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal User user
    ) throws IOException {

        String newImageUrl = userService.updateProfileImage(user, image);

        // 프론트엔드에 새 이미지 URL을 JSON 형식으로 반환
        return ResponseEntity.ok(Map.of("profileImageUrl", newImageUrl));
    }
}