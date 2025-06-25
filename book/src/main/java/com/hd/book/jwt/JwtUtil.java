package com.hd.book.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    // 액세스 토큰 설정
    @Value(value = "${jwt.secret:ChangeThisSecretKeyForProd}") // 서명용 비밀키
    private String secretKey;

    @Value(value = "${jwt.expiration-in-ms:3600000}") // 1시간
    private long accessTokenValidityInMs;

    // 리프레시 토큰 설정
    @Value("${jwt.refresh-expiration-in-ms:604800000}") // 7일
    private long refreshTokenValidityInMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 액세스 토큰 생성
    public String generateToken(String userEmail) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMs);
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String userEmail) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMs);
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 사용자 이메일 추출
    public String getUserEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰에서 사용자 고유 ID 추출
    public Long getUserId(String token) {
        String sub = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(sub);
    }

    // RefreshToken의 유효기간(ms) 값 반환
    public long getRefreshTokenValidityInMs() {
        return this.refreshTokenValidityInMs;
    }
}