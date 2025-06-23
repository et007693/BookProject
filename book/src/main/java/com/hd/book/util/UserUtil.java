package com.hd.book.util;

import com.hd.book.entity.UserEntity;
import com.hd.book.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final UserRepository userRepository;

    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserEmail() {
        // 현재 SecurityContext에 저장된 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }

        return authentication.getName(); // JWT에서 추출한 이메일
    }

    public UserEntity getUser() {
        return userRepository.findByEmail(getUserEmail())
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
    }
}
