package com.hd.book.repository;

import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // 이메일 존재 여부 확인 ? true(있) : false(없)
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인 ? true(있) : false(없)
    boolean existsByNickname(String nickname);
    
    // 이메일로 유저 정보 조회
    Optional<UserEntity> findByEmail(String email);
}
