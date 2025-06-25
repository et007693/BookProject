package com.hd.book.service;

import com.hd.book.entity.RefreshTokenEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.jwt.JwtUtil;
import com.hd.book.repository.RefreshTokenRepository;
import com.hd.book.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 사용자 이메일로 리프레시 토큰을 생성하고 DB에 저장
    public RefreshTokenEntity createRefreshToken(String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. : " + userEmail));

        // 기존에 발급된 리프레시 토큰이 있으면 삭제
        refreshTokenRepository.deleteByUser(user);

        // 새로운 리프레쉬 토큰 문자열 생성
        String token = jwtUtil.generateRefreshToken(userEmail);

        // 만료 시각
        Instant expiry = Instant.now().plusMillis(jwtUtil.getRefreshTokenValidityInMs());

        // 엔티티에 값 세팅
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setToken(token);
        entity.setExpiryDate(expiry);

        // DB 저장 후 반환
        return refreshTokenRepository.save(entity);
    }

    // 리프레시 토큰의 만료 여부 확인
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity tokenEntity) {
        // 만료 시각이 현재 시각 이전이면 토큰 만료
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            // 만료된 토큰은 DB에서 삭제
            refreshTokenRepository.delete(tokenEntity);
            // 호출 측에 토큰 만료 예외 전파
            throw new IllegalArgumentException("Refresh token expired");
        }
        // 유효한 토큰이면 그대로 반환
        return tokenEntity;
    }

    // 토큰 문자열로 DB에서 리프레시 토큰 엔티티를 조회
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // 로그아웃 메서드
    @Transactional
    public void deleteByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        refreshTokenRepository.deleteByUser(user);
    }
}
