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
    public void postBookComment(BookCommentReqDto bookCommentReqDto) {
        BookCommentEntity comment = convertDtoToEntity(bookCommentReqDto);
        bookCommentRepository.save(comment);
    }

    // 댓글 수정
    public void updateBookComment(Long commentId, BookCommentReqDto bookCommentReqDto) {
        BookCommentEntity comment = bookCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다."));
        if (!userUtil.getUser().getUserId().equals(comment.getUser().getUserId())) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }
        comment.setContent(bookCommentReqDto.getContent());
        comment.setRate(bookCommentReqDto.getRate());
        comment.setBook(bookService.getOrCreateBook(bookCommentReqDto.getIsbn()));
        bookCommentRepository.save(comment);
    }

    // dto -> entity
    private BookCommentEntity convertDtoToEntity(BookCommentReqDto bookCommentReqDto) {
        UserEntity user = userUtil.getUser();
        BookEntity book = bookService.getOrCreateBook(bookCommentReqDto.getIsbn());
        BookCommentEntity bookCommentEntity = new BookCommentEntity();
        bookCommentEntity.setUser(user);
        bookCommentEntity.setBook(book);
        bookCommentEntity.setContent(bookCommentReqDto.getContent());
        bookCommentEntity.setRate(bookCommentReqDto.getRate());
        return bookCommentEntity;
    }
}
