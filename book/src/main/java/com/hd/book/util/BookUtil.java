package com.hd.book.util;

import com.hd.book.entity.BookEntity;
import com.hd.book.repository.BookRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BookUtil {
    private final BookRepository bookRepository;

    public BookUtil(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookEntity getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("책 정보를 찾을 수 없습니다."));
    }
}
