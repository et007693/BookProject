package com.hd.book.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookCommentReqDto {
    private String isbn;
    private String content;
    private Integer rate;
}
