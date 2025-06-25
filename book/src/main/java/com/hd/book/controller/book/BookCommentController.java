package com.hd.book.controller.book;

import com.hd.book.dto.comment.BookCommentReqDto;
import com.hd.book.dto.comment.BookCommentResDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.service.BookCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/book/comment")
@RequiredArgsConstructor
public class BookCommentController {
    private final BookCommentService bookCommentService;

    // 댓글 작성
    @PostMapping("/post")
    public ResponseEntity<ApiResponseDto<Void>> postBookComment(@RequestBody BookCommentReqDto bookCommentReqDto) {
        try {
            bookCommentService.postBookComment(bookCommentReqDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 작성 성공", null));
        } catch (Exception e) {
            log.error("댓글 등록에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 댓글 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<ApiResponseDto<Void>> updateBookComment(@PathVariable Long commentId, @RequestBody BookCommentReqDto bookCommentReqDto) {
        try {
            bookCommentService.updateBookComment(commentId, bookCommentReqDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 수정 성공", null));
        } catch (Exception e) {
            log.error("댓글 수정에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 댓글 삭제
    @DeleteMapping("delete/{commentId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBookComment(@PathVariable Long commentId) {
        try {
            bookCommentService.deleteBookComment(commentId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 삭제 성공", null));
        } catch (Exception e) {
            log.error("댓글 삭제에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 댓글 목록
    @GetMapping("{isbn}/list")
    public ResponseEntity<ApiResponseDto<List<BookCommentResDto>>> getBookComments (@PathVariable String isbn) {
        try {
            List<BookCommentResDto> comments = bookCommentService.getBookComments(isbn);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글 조회 성공", comments));
        } catch (Exception e) {
            log.error("댓글 조회에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}
