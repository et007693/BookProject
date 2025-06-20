package com.hd.book.repository;

import com.hd.book.entity.BoardCommentEntity;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity, Long> {
    // 책의 전체 댓글
    List<BoardCommentEntity> findByBookIsbn(String isbn);
}