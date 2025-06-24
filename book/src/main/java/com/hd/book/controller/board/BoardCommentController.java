package com.hd.book.controller.board;

import com.hd.book.dto.comment.BoardCommentWriteDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardId}")
public class BoardCommentController {
    private final BoardCommentService boardCommentService;

    @PostMapping("/comment")
    public ResponseEntity<ApiResponseDto<Void>> postBoardComment(@PathVariable Long boardId, @RequestBody BoardCommentWriteDto boardCommentWriteDto) {
        try {
            boardCommentService.postBoardComment(boardId, boardCommentWriteDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 등록 성공", null));
        } catch (Exception e) {
            log.error("댓글 등록에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}
