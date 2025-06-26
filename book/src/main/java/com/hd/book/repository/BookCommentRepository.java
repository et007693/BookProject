package com.hd.book.repository;

import com.hd.book.entity.BookCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCommentRepository extends JpaRepository<BookCommentEntity, Long> {

    List<BookCommentEntity> findByBookIsbn(String isbn);

    Optional<BookCommentEntity> findByBook_IsbnAndUser_UserId(String isbn, Long userId);
}