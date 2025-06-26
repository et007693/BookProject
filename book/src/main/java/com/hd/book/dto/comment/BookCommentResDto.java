package com.hd.book.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder
public class BookCommentResDto {
    private Long commentId;
    private String username;
    private String content;
    private Integer rate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}