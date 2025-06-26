package com.hd.book.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardCommentResDto {
    Long boardCid;
    String comment;
    Integer likeCount;
    Integer hateCount;
    LocalDateTime createdAt;
    Long userId;
    String username;
}
