package com.hd.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookRepository, Long> {
    // isbn 존재 여부 확인
    boolean existsByIsbn(String isbn);
}
