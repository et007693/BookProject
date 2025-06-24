package com.hd.book.repository;

import com.hd.book.constant.HistoryStatus;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.HistoryEntity;
import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    // 유저의 모든 이력 조회
    List<HistoryEntity> findByUserUserId(Long userId);

    // 이미 기존에 기록이 있는지 확인
    boolean existsByBookIsbn(String isbn);

    // 상태별 조회
    List<HistoryEntity> findByUserUserIdAndStatus(Long UserId, String status);

    boolean existsByUserAndBook(UserEntity user, BookEntity book);
}
