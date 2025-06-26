package com.hd.book.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BoardCommentReactionResDto {
    private int likeCount;
    private int hateCount;
}
