package com.hd.book.controller;

import com.hd.book.dto.auth.JwtResponseDto;
import com.hd.book.dto.auth.LoginRequestDto;
import com.hd.book.dto.auth.SignupRequestDto;
import com.hd.book.jwt.JwtUtil;
import com.hd.book.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

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
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            String token = jwtUtil.generateToken(authentication.getName());
            log.info("[INFO] 로그인에 성공했습니다.");
            return ResponseEntity.ok(new JwtResponseDto(token));
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
}

