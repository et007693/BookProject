package com.hd.book.controller.board;

import com.hd.book.constant.ReactionType;
import com.hd.book.dto.comment.BoardCommentReactionResDto;
import com.hd.book.dto.comment.BoardCommentResDto;
import com.hd.book.dto.comment.BoardCommentWriteDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.repository.BoardRepository;
import com.hd.book.service.BoardCommentReactionService;
import com.hd.book.service.BoardCommentService;
import com.hd.book.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardId}/comment")
// TODO: 수정하는 사람과 댓글을 쓴 유저가 같은지 확인하는 로직 작성
public class BoardCommentController {
    private final BoardCommentReactionService boardCommentReactionService;
    private final BoardCommentService boardCommentService;
    private final BoardRepository boardRepository;
    private final UserUtil userUtil;

    // 댓글 등록
    @PostMapping("/post")
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
    @GetMapping("/list")
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

    // 댓글 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<ApiResponseDto<Void>> updateBoardComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @RequestBody BoardCommentWriteDto boardCommentWriteDto) {
        try{
            boardCommentService.updateBoardComment(commentId, boardCommentWriteDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 수정 성공", null));
        } catch (Exception e) {
            log.error("댓글 수정에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBoardComment (@PathVariable Long commentId) {
        try {
            boardCommentService.deleteBoardComment(commentId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 삭제 성공", null));
        } catch (Exception e) {
            log.error("댓글 삭제에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 댓글 반응 클릭
    @PostMapping("{commentId}/{type}")
    public ResponseEntity<ApiResponseDto<BoardCommentReactionResDto>> reactComment(
            @PathVariable Long commentId,
            @PathVariable ReactionType type
        )
    {
        try {
            BoardCommentReactionResDto reaction = boardCommentReactionService.clickCommentReaction(commentId, type);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "좋아요/싫어요가 반영되었습니다.", reaction));
        } catch (Exception e) {
            log.error("오류가 발생하였습니다. {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }

    }
}
