package com.hd.book.repository;

import com.hd.book.constant.BoardType;
import com.hd.book.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // 게시판 타입별 게시글 조회
    Page<BoardEntity> findByType(BoardType boardType, Pageable pageable);

    // 제목 키워드 검색
    Page<BoardEntity> findByTitleContaining(String keyword, Pageable pageable);

    // 유저 게시글 검색
    Page<BoardEntity> findByUserUserId(Long userId, Pageable pageable);

    // 책 게시글 검색
    Page<BoardEntity> findByBookIsbn(String isbn, Pageable pageable);

    // 게시글 페이지네이션
    Page<BoardEntity> findAll(Pageable pageable);

    // 좋아요 상위
    Page<BoardEntity> findAllByOrderByLikeCountDesc(Pageable pageable);

    // 최근 작성순
    Page<BoardEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}
