package com.example.GreenCloset.jwt;

import com.example.GreenCloset.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // 1. Spring Bean으로 등록
public class JwtUtil {

    // 2. application.properties에서 값 주입
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.ms}")
    private long expirationTime;

    // 3. HMAC-SHA 알고리즘에 사용할 Key 객체
    private Key key;

    // 4. 쿠키 이름
    public static final String AUTH_COOKIE_NAME = "auth_token";

    // 5. Bean 생성 후 비밀 키를 Key 객체로 변환
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 6. 토큰 생성 (Email 기반)
     */
    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(email) // 사용자의 이메일을 Subject로 저장
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 Key로 서명
                .compact();
    }

    /**
     * 7. 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // (로그) log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 8. 토큰에서 Email(Subject) 추출
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 9. HttpOnly 쿠키에 토큰 추가 (로그인/회원가입 응답)
     */
    public void addTokenToCookie(String token, HttpServletResponse response) {
        // (토큰 값에 URL-unsafe 문자가 있을 수 있으므로 인코딩)
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, encodedToken);
        cookie.setHttpOnly(true); // JavaScript 접근 방지 (XSS)
        cookie.setSecure(false);   // (TODO: HTTPS 적용 후 true로 변경)
        cookie.setPath("/");       // 모든 경로에서 쿠키 사용
        cookie.setMaxAge((int) (expirationTime / 1000)); // 쿠키 만료 시간 (초)

        response.addCookie(cookie);
    }

    /**
     * 10. 쿠키 만료 (로그아웃 응답)
     */
    public void expireTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, null);
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 11. 요청(Request)의 쿠키에서 토큰 가져오기 (인증 필터)
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                    return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }
}