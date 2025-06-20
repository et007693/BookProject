package com.hd.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardWriteDto {
    private Long boardId;
    private String type;
    private String title;
    private String content;
    private String image;
    private Integer like;
    private LocalDateTime updated_at;
    private String isbn;
}
