package com.hd.book.dto.board;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardWriteDto {
    private String type;
    private String title;
    private String content;
//    private String image;
    private String isbn;
}
