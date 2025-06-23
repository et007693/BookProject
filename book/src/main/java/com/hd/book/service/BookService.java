package com.hd.book.service;

import com.hd.book.entity.BookEntity;
import com.hd.book.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Book;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Transactional
    public BookEntity getOrCreateBook(String isbn) {
        return bookRepository.findById(isbn)
                .orElseGet(() -> {
                    BookEntity book = new BookEntity();
                    book.setIsbn(isbn);
                    return bookRepository.save(book);
        });
    }
}
