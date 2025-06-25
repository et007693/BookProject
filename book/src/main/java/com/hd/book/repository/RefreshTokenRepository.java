package com.hd.book.repository;

import com.hd.book.entity.RefreshTokenEntity;
import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    int deleteByUser(UserEntity user);
}
