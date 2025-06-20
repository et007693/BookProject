package com.hd.book.repository;

import com.hd.book.entity.BookCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCommentRepository extends JpaRepository<BookCommentEntity, Long> {
    // 책의 전체 댓글 조회
    List<BookCommentEntity> findByBookIsbn(String isbn);

    //
}
