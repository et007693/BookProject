package com.hd.book.controller;

import com.hd.book.dto.auth.JwtResponseDto;
import com.hd.book.dto.auth.LoginRequestDto;
import com.hd.book.dto.auth.SignupRequestDto;
import com.hd.book.jwt.JwtUtil;
import com.hd.book.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Validated @RequestBody SignupRequestDto dto
    ) {
        userService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
        String token = jwtUtil.generateToken(authentication.getName());
        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}

