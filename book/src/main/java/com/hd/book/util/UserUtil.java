package com.hd.book.util;

import com.hd.book.entity.UserEntity;
import com.hd.book.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final UserRepository userRepository;

    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public UserEntity gettUserByEmail() {
        return userRepository.findByEmail(getUserEmail())
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
    }
}
