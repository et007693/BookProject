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

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
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

    @Transactional
    public UserProfileDto updateMyProfile(String email, UserProfileDto userProfileDto) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. email=" + email));

        if (userProfileDto.getNickname() != null) user.setNickname(userProfileDto.getNickname());
        if (userProfileDto.getBio() != null) user.setBio(userProfileDto.getBio());
        if (userProfileDto.getProfileImage() != null) user.setProfileImage(userProfileDto.getProfileImage());
        if (userProfileDto.getReadPublic() != null) user.setReadPublic(userProfileDto.getReadPublic());

        return UserProfileDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .readPublic(user.isReadPublic())     // Boolean 리턴하는 엔티티 메서드
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    @Transactional
    public void deleteByEmail(String email) {
        // 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("삭제할 유저를 찾을 수 없습니다.")
                );

        // 삭제
        userRepository.delete(user);
        log.info("사용자 삭제 완료: userId={}, email={}", user.getUserId(), user.getEmail());
    }
}