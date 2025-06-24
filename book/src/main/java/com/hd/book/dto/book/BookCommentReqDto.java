package com.hd.book.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookCommentReqDto {
    private String content;
    private Integer rate;
}
