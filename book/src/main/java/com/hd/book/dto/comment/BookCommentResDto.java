package com.hd.book.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class BookCommentResDto {
    private Long commentId;
    private String username;
    private String content;
    private Integer rate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}