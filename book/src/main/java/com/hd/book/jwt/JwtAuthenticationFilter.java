package com.hd.book.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        // Authorization 헤더에서 토큰 꺼내기
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // 토큰 유효성 검사
                if (jwtUtil.validateToken(token)) {
                    // 사용자 정보 추출
                    String username = jwtUtil.getUserEmail(token);
                    // 인증 객체 생성 및 SecurityContext에 등록
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList());
                    ((UsernamePasswordAuthenticationToken) auth)
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.error("Cannot set user authentication: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
