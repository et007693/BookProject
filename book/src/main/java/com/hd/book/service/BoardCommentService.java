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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardCommentService {
    private final UserUtil userUtil;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;

    // 댓글 등록
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

    // 댓글 목록 조회
    public List<BoardCommentResDto> getBoardComments(Long boardId) {
        List<BoardCommentEntity> comments = boardCommentRepository.findByBoardBoardId(boardId);
        List<BoardCommentResDto> response = new ArrayList<>();
        for (BoardCommentEntity comment: comments) {
            response.add(convertEntityToDto(comment));
        }
        return response;
    }

    // 댓글 수정
    public void updateBoardComment(Long commentId, BoardCommentWriteDto boardCommentWriteDto) {
        BoardCommentEntity comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글이 존재하지 않습니다"));
        comment.setContent(boardCommentWriteDto.getContent());
        boardCommentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteBoardComment(Long commentId) {
        BoardCommentEntity comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글이 존재하지 않습니다."));
        boardCommentRepository.delete(comment);
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

    // Entity -> Dto
    private BoardCommentResDto convertEntityToDto(BoardCommentEntity boardCommentEntity) {
        BoardCommentResDto comment = new BoardCommentResDto();
        comment.setBoardCid(boardCommentEntity.getBoardCid());
        comment.setComment(boardCommentEntity.getContent());
        comment.setLike(boardCommentEntity.getLikeCount());
        comment.setHate(boardCommentEntity.getHateCount());
        comment.setCreatedAt(boardCommentEntity.getCreatedAt());
        comment.setUserId(boardCommentEntity.getUser().getUserId());
        return comment;
    }
}
