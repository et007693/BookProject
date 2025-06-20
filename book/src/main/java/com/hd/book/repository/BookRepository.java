package com.hd.book.repository;

import com.hd.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {
    // isbn으로 책 검색
    Optional<BookEntity> findByIsbn(String isbn);

    // isbn 존재 여부 확인
    boolean existsByIsbn(String isbn);
}
