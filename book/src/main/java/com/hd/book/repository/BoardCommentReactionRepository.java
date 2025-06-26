package com.hd.book.repository;

import com.hd.book.constant.ReactionType;
import com.hd.book.entity.BoardCommentEntity;
import com.hd.book.entity.BoardCommentReactionEntity;
import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardCommentReactionRepository extends JpaRepository<BoardCommentReactionEntity, Long> {
    // user + comment 반응 검색
    Optional<BoardCommentReactionEntity> findByUserAndBoardComment(UserEntity user, BoardCommentEntity comment);

    // 댓글 타입별 갯수
    Integer countByBoardCommentAndReactionType(BoardCommentEntity boardCommentEntity, ReactionType reactionType);
}
