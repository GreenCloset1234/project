package com.example.GreenCloset.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. "/api/**"로 시작하는 모든 요청에 대해


                .allowedOrigins("http://localhost:5173")

                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메소드
                .allowedHeaders("*") // 4. 모든 헤더 허용
                .allowCredentials(true) // 5. 쿠키 인증 요청 허용
                .maxAge(3600);
    }
}