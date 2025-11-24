package com.example.GreenCloset;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class GreenClosetApplication {

    @PostConstruct
    public void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("현재 시간대: " + TimeZone.getDefault().getID()); // 로그 확인용
    }

	public static void main(String[] args) {
		SpringApplication.run(GreenClosetApplication.class, args);
	}

}
