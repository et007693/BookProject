package com.hd.book.controller;

import com.hd.book.dto.auth.JwtResponseDto;
import com.hd.book.dto.auth.LoginRequestDto;
import com.hd.book.dto.auth.LoginResponseDto;
import com.hd.book.dto.auth.SignupRequestDto;
import com.hd.book.entity.RefreshTokenEntity;
import com.hd.book.jwt.JwtUtil;
import com.hd.book.service.RefreshTokenService;
import com.hd.book.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<Void> register(
            @Validated @RequestBody SignupRequestDto dto
    ) {
        try {
            userService.register(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg.contains("이메일")) {
                // 이메일 중복
                return ResponseEntity
                        .badRequest()
                        .header("Error-Message", "이미 등록된 이메일입니다.")
                        .build();
            } else if (msg.contains("닉네임")) {
                // 닉네임 중복
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)   // 409 Conflict
                        .header("Error-Message", "이미 사용 중인 닉네임입니다.")
                        .build();
            } else {
                // 기타 IllegalArgumentException
                return ResponseEntity
                        .badRequest()
                        .header("Error-Message", msg)
                        .build();
            }
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto request
    ) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            // 엑세스 토큰
            String token = jwtUtil.generateToken(authentication.getName());

            // 리프레시 토큰
            String refreshToken = refreshTokenService
                    .createRefreshToken(authentication.getName())
                    .getToken();

            // 사용자 닉네임 조회
            String email = authentication.getName();
            String nickname = userService
                    .getMyProfile(email)
                    .getNickname();

            log.info("[INFO] 로그인 및 토큰 발급에 성공했습니다.");

            return ResponseEntity.ok(new LoginResponseDto(token, refreshToken, nickname));
        } catch (AuthenticationException ex) {
            // 인증 실패
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // 사용자로부터 받은 리프레시 토큰으로 새로운 엑세스, 리프레시 토큰 발급
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(
            @RequestBody Map<String, String> body) {
        String requestRefreshToken = body.get("refreshToken");

        // 1) DB 조회
        RefreshTokenEntity storedToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new BadCredentialsException("리프레시 토큰을 찾을 수 없습니다.") {
                });
        // 2) 만료 여부 검증 (만료 시 예외 발생)
        refreshTokenService.verifyExpiration(storedToken);

        // 3) 새 액세스 토큰 생성
        String newAccessToken = jwtUtil.generateToken(storedToken.getUser().getEmail());
        // 4) 새 리프레시 토큰도 재발급
        String newRefreshToken = refreshTokenService
                .createRefreshToken(storedToken.getUser().getEmail())
                .getToken();

        return ResponseEntity.ok(
                new JwtResponseDto(newAccessToken, newRefreshToken));
    }

    // 사용자 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        refreshTokenService.deleteByEmail(email);
        return ResponseEntity.ok().build();
    }
}

