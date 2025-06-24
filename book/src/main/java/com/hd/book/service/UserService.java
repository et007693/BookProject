package com.hd.book.service;

import com.hd.book.dto.auth.SignupRequestDto;
import com.hd.book.dto.user.UserProfileDto;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity register(SignupRequestDto dto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 엔티티 생성
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setProfileImage(dto.getProfileImage());

        // 저장 및 반환
        UserEntity saved = userRepository.save(user);

        log.info("회원가입 성공: userId={}, email={}",
                saved.getUserId(), saved.getEmail());
        return saved;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 고유 아이디를 찾을 수 없습니다."));
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .readPublic(user.isReadPublic())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}