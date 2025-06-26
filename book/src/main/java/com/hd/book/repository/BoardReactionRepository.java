package com.hd.book.repository;

import com.hd.book.constant.ReactionType;
import com.hd.book.entity.BoardEntity;
import com.hd.book.entity.BoardReactionEntity;
import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardReactionRepository extends JpaRepository<BoardReactionEntity, Long> {
    // user + board로 entity 반환
    Optional<BoardReactionEntity> findByUserAndBoard(UserEntity user, BoardEntity board);

    // 기존 좋아요 클릭 여부
    Boolean existsByUserAndBoard(UserEntity user, BoardEntity board);

    // 특정 게시판의 좋아요 갯수
    int countByBoardBoardIdAndReactionType(Long boardId, ReactionType reactionType);
}
