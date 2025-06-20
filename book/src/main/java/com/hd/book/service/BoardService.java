package com.hd.book.service;

import com.hd.book.dto.BoardWriteDto;
import com.hd.book.entity.BoardEntity;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BookRepository;
import com.hd.book.repository.UserRepository;
import com.hd.book.util.BookUtil;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserUtil userUtil;
    private final BookUtil bookUtil;

    // 게시글 등록
    public boolean postBoard(BoardWriteDto boardWriteDto) {
        try {
            UserEntity user = userUtil.getCurrentUser();

            BoardEntity board = convertDto
        } catch (Exception e) {
            log.error("");
        }
        return false;
    }

    // DTO -> Entity 변환
     private BoardEntity convertDtoToEntity(BoardWriteDto boardWriteDto) {
         UserEntity user = userUtil.gettUserByEmail();
         BookEntity book = bookUtil.getBookByIsbn(boardWriteDto.getIsbn());

         BoardEntity board = new BoardEntity();
         board.setType(boardWriteDto.getType());
         board.setTitle(boardWriteDto.getTitle());
         board.setContent(boardWriteDto.getContent());
         board.setImage(boardWriteDto.getImage());
         board.setLikeCount(boardWriteDto.getLike());
         board.setUser(user);
         board.setBook(book);
     }
}
