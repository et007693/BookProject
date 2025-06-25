package com.hd.book.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardCommentResDto {
    Long boardCid;
    String comment;
    Integer like;
    Integer hate;
    LocalDateTime createdAt;
    Long userId;
    String username;
}
