package com.hd.book.dto.board;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardResDto {
    private Long boardId;
    private String type;
    private String title;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
    //    private String image;
    private String isbn;
    private Long userId;
}
