package com.hd.book.dto.comment;

import com.hd.book.constant.ReactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardCommentResDto {
    Long boardCid;
    String reaction;
    String comment;
    Integer likeCount;
    Integer hateCount;
    LocalDateTime createdAt;
    Long userId;
    String username;
}
