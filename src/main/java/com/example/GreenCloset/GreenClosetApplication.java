package com.example.GreenCloset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GreenClosetApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenClosetApplication.class, args);
	}

}
