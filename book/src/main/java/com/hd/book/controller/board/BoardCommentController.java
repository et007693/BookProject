package com.hd.book.controller.board;

import com.hd.book.dto.comment.BoardCommentResDto;
import com.hd.book.dto.comment.BoardCommentWriteDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.repository.BoardRepository;
import com.hd.book.service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardId}")
public class BoardCommentController {
    private final BoardCommentService boardCommentService;
    private final BoardRepository boardRepository;

    // 댓글 등록
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

    // 댓글 조회
    @GetMapping("/comment/list")
    public ResponseEntity<ApiResponseDto<List<BoardCommentResDto>>> getBoardComment(@PathVariable Long boardId) {
        try {
            if (!boardRepository.existsById(boardId)) {
                throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
            }
            List<BoardCommentResDto> comments = boardCommentService.getBoardComments(boardId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 조회 성공", comments));
        } catch (Exception e) {
            log.error("댓글 조회에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}
