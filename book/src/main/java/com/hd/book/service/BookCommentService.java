package com.hd.book.service;

import com.hd.book.dto.book.BookCommentReqDto;
import com.hd.book.entity.BoardCommentEntity;
import com.hd.book.entity.BookCommentEntity;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BookCommentRepository;
import com.hd.book.repository.BookRepository;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class BookCommentService {
    private final BookCommentRepository bookCommentRepository;
    private final BookService bookService;
    private final UserUtil userUtil;

    // 댓글 작성
    public void postBookComment(String isbn, BookCommentReqDto bookCommentReqDto) {
        BookCommentEntity comment = convertDtoToEntity(isbn, bookCommentReqDto);
        bookCommentRepository.save(comment);
    }

    // dto -> entity
    private BookCommentEntity convertDtoToEntity(String isbn, BookCommentReqDto bookCommentReqDto) {
        UserEntity user = userUtil.getUser();
        BookEntity book = bookService.getOrCreateBook(isbn);
        BookCommentEntity bookCommentEntity = new BookCommentEntity();
        bookCommentEntity.setUser(user);
        bookCommentEntity.setBook(book);
        bookCommentEntity.setContent(bookCommentReqDto.getContent());
        bookCommentEntity.setRate(bookCommentReqDto.getRate());
        return bookCommentEntity;
    }
}
