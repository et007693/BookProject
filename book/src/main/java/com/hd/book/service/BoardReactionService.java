package com.hd.book.service;

import com.hd.book.constant.ReactionType;
import com.hd.book.entity.BoardEntity;
import com.hd.book.entity.BoardReactionEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BoardReactionRepository;
import com.hd.book.repository.BoardRepository;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class BoardReactionService {
    private final UserUtil userUtil;
    private final BoardRepository boardRepository;
    private final BoardReactionRepository boardReactionRepository;

    // 게시글 좋아요 클릭 기능
    public Integer toggleLike(Long boardId) {
        UserEntity user = userUtil.getUser();
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("해당 게시물이 존재하지 않습니다."));
        // 기존 좋아요 했는지 확인
        Optional<BoardReactionEntity> exiting = boardReactionRepository.findByUserAndBoard(user, board);

        // 좋아요를 눌렀다면
        if (exiting.isPresent()) {
            boardReactionRepository.delete(exiting.get());
        } else {
            BoardReactionEntity reaction = new BoardReactionEntity();
            reaction.setUser(user);
            reaction.setBoard(board);
            reaction.setReactionType(ReactionType.LIKE);
            boardReactionRepository.save(reaction);
        }
        board.setLikeCount(boardReactionRepository.countByBoardBoardIdAndReactionType(boardId, ReactionType.LIKE));
        return board.getLikeCount();
    }
}
