package com.hd.book.service;

import com.hd.book.dto.comment.BookCommentReqDto;
import com.hd.book.dto.comment.BookCommentResDto;
import com.hd.book.entity.BookCommentEntity;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BookCommentRepository;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
// TODO: build 적용
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

    // 댓글 삭제
    public void deleteBookComment(Long commentId) {
        BookCommentEntity comment = bookCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다."));
        if (!userUtil.getUser().getUserId().equals(comment.getUser().getUserId())) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }
        bookCommentRepository.delete(comment);
    }

    // 댓글 목록
    public List<BookCommentResDto> getBookComments(String isbn) {
        List<BookCommentResDto> response = new ArrayList<>();
        List<BookCommentEntity> comments = bookCommentRepository.findByBookIsbn(isbn);

        for (BookCommentEntity comment: comments) {
            response.add(convertEntityToDto(comment));
        }
        return response;
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

    // entity -> dto
    private BookCommentResDto convertEntityToDto(BookCommentEntity bookCommentEntity) {
        return BookCommentResDto.builder()
                .commentId(userUtil.getUser().getUserId())
                .username(userUtil.getUser().getNickname())
                .content(bookCommentEntity.getContent())
                .rate(bookCommentEntity.getRate())
                .createdAt(bookCommentEntity.getCreatedAt())
                .updatedAt(bookCommentEntity.getUpdatedAt())
                .build();
    }
}
