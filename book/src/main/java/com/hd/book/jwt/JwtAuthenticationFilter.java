package com.hd.book.jwt;

import com.hd.book.service.CustomDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomDetailsService customDetailsService; // CustomDetailsService 주입

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
                    // 사용자 이메일(JWT sub 클레임) 추출
                    String userEmail = jwtUtil.getUserEmail(token); // getUserEmail은 토큰에서 이메일을 추출할 것으로 예상

                    // UserDetailsService를 통해 UserDetails 객체 로드
                    // 이 userDetails 객체가 바로 컨트롤러로 주입될 객체입니다.
                    UserDetails userDetails = customDetailsService.loadUserByUsername(userEmail);

                    // 인증 객체 생성 및 SecurityContext에 등록
                    // principal에 UserDetails 객체를 넣어줍니다.
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()); // UserDetails 객체와 권한을 사용

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 인증 과정에서 발생한 예외 로깅 (예: 토큰 만료, 잘못된 토큰, 사용자 없음 등)
                log.error("Could not set user authentication in security context: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}