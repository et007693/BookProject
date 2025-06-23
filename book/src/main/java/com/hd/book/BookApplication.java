package com.hd.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // @Bean 어노테이션 임포트
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BookApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookApplication.class, args);
	}

	// CORS 설정을 위한 Bean 정의
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**") // 🚨 주의: 백엔드 API의 실제 기본 경로에 맞춰 변경하세요.
						// 예를 들어, 모든 API가 '/api'로 시작한다면 '/api/**'
						// 만약 API가 '/users', '/products' 등이라면 각각을 추가하거나 '/**'로 모든 경로 허용
						.allowedOrigins("http://localhost:5173") // 🚨 Vue 개발 서버의 포트에 맞춰 변경하세요! (8080일 수도 있음)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
						.allowedHeaders("*") // 모든 헤더 허용
						.allowCredentials(true) // 자격 증명 (쿠키, HTTP 인증) 허용 여부 (필요한 경우)
						.maxAge(3600); // Pre-flight 요청 결과를 캐싱할 시간 (초)
			}
		};
	}
}