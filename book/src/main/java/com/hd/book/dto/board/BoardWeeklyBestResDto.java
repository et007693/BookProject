package com.hd.book.dto.board;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BoardWeeklyBestResDto {
    private Long boardId;;
    private String type;
    private String title;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private String isbn;
    private String userEmail;
    private String nickname;
}
