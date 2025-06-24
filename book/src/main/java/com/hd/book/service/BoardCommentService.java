package com.hd.book.service;

import com.hd.book.dto.comment.BoardCommentResDto;
import com.hd.book.dto.comment.BoardCommentWriteDto;
import com.hd.book.entity.BoardCommentEntity;
import com.hd.book.entity.BoardEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BoardCommentRepository;
import com.hd.book.repository.BoardRepository;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {
    private final UserUtil userUtil;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;

    public void postBoardComment(Long boardId, BoardCommentWriteDto boardCommentWriteDto) {
        UserEntity user = userUtil.getUser();
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다"));
        BoardCommentEntity comment = new BoardCommentEntity();
        comment.setUser(user);
        comment.setBoard(board);
        comment.setContent(boardCommentWriteDto.getContent());
        boardCommentRepository.save(comment);
    }

    // DTO -> Entity
    private BoardCommentEntity convertDtoToEntity(Long boardId, BoardCommentWriteDto boardCommentWriteDto) {
        UserEntity user = userUtil.getUser();
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다"));
        BoardCommentEntity comment = new BoardCommentEntity();
        comment.setContent(boardCommentWriteDto.getContent());
        comment.setUser(user);
        comment.setBoard(board);
        return comment;
    }
    
}
