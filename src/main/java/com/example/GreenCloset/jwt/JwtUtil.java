package com.example.GreenCloset.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm; // [수정] SignatureAlgorithm import
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24; // 24시간

    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * JWT 토큰 생성 (0.11.5 API)
     */
    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);

        // [수정] 0.11.5 버전 문법 (set... / signWith(Key))
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(this.key, SignatureAlgorithm.HS256) // [수정] 알고리즘 명시
                .compact();
    }

    /**
     * JWT 토큰 검증 및 Claims 추출 (0.11.5 API)
     */
    private Claims getClaimsFromToken(String token) {
        // [수정] 0.11.5 버전 문법 (parserBuilder() / setSigningKey())
        // (이것이 0.12.5의 parser().verifyWith()와 기능이 같습니다)
        return Jwts.parserBuilder()
                .setSigningKey(this.key) // [수정] setSigningKey(key) 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 사용자 이메일(Subject) 추출
     */
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            // (로그) log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}