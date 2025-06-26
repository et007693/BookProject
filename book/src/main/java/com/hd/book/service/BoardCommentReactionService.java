package com.hd.book.service;

import com.hd.book.constant.ReactionType;
import com.hd.book.dto.comment.BoardCommentReactionResDto;
import com.hd.book.entity.BoardCommentEntity;
import com.hd.book.entity.BoardCommentReactionEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BoardCommentReactionRepository;
import com.hd.book.repository.BoardCommentRepository;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardCommentReactionService {
    private final BoardCommentReactionRepository boardCommentReactionRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final UserUtil userUtil;

    // 댓글 반응 클릭
    public BoardCommentReactionResDto clickCommentReaction(Long commentId, ReactionType reactionType) {
        UserEntity user = userUtil.getUser();
        BoardCommentEntity comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글이 존재하지 않습니다."));
        Optional<BoardCommentReactionEntity> existing = boardCommentReactionRepository.findByUserAndBoardComment(user, comment);

        if (existing.isPresent()) {
            ReactionType before = existing.get().getReactionType();
            if (before.equals(reactionType)) {
                boardCommentReactionRepository.delete(existing.get());
            } else {
                existing.get().setReactionType(reactionType);
            }
        } else {
            BoardCommentReactionEntity reaction = new BoardCommentReactionEntity();
            reaction.setUser(user);
            reaction.setBoardComment(comment);
            reaction.setReactionType(reactionType);
            boardCommentReactionRepository.save(reaction);
        }
        comment.setLikeCount(boardCommentReactionRepository.countByBoardCommentAndReactionType(comment, ReactionType.LIKE));
        comment.setHateCount(boardCommentReactionRepository.countByBoardCommentAndReactionType(comment, ReactionType.DISLIKE));
        boardCommentRepository.save(comment);

        return new BoardCommentReactionResDto(comment.getLikeCount(), comment.getHateCount());

    }
}
