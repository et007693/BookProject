package com.hd.book.dto.board;

import com.hd.book.entity.BoardEntity;
import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardLikeGroupResDto {
    private List<BoardResDto> bookBoards;
    private List<BoardResDto> forumBoards;
}
