package com.hd.book.controller.board;

import com.hd.book.dto.board.BoardLikeGroupResDto;
import com.hd.book.dto.board.BoardResDto;
import com.hd.book.dto.board.BoardWeeklyBestResDto;
import com.hd.book.dto.board.BoardWriteDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.service.BoardReactionService;
import com.hd.book.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;
    private final BoardReactionService boardReactionService;

    //게시글 등록
    @PostMapping("/post")
    public ResponseEntity<ApiResponseDto<Void>> postBoard(@RequestBody BoardWriteDto dto) {
        try {
            boardService.postBoard(dto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시글 등록 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "알 수 없는 오류가 발생했습니다.", null));
        }
    }

    // 게시글 수정
    @PutMapping("/update/{boardId}")
    public ResponseEntity<ApiResponseDto<Void>> updateBoard(@PathVariable Long boardId, @RequestBody BoardWriteDto dto) {
        try {
            boardService.updateBoard(boardId, dto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시글 수정 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "알 수 없는 오류가 발생했습니다.", null));
        }
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBoard(@PathVariable Long boardId) {
        try {
            boardService.deleteBoard(boardId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시글 삭제 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "알 수 없는 오류가 발생했습니다.", null));
        }
    }

    // 게시물 목록
    @GetMapping("/list")
    public ResponseEntity<ApiResponseDto<Page<BoardResDto>>> boardList(@PageableDefault(size = 10000, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<BoardResDto> response = boardService.boardList(pageable);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시글 목록 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "알 수 없는 오류가 발생했습니다.", null));
        }
    }

    // 게시글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponseDto<BoardResDto>> boardDetail(@PathVariable Long boardId) {
        try {
            BoardResDto response = boardService.boardDetail(boardId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시글 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "알 수 없는 오류가 발생했습니다.", null));
        }
    }

    // 도서로 게시글 조회
    // TODO: 게시글 있는지 여부에 따라 오류 메시지
    @GetMapping("/list/{isbn}")
    public ResponseEntity<ApiResponseDto<Page<BoardResDto>>> bookBoardList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable String isbn) {
        try {
            Page<BoardResDto> boards = boardService.bookBoardList(isbn, pageable);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시글 조회 성공", boards));
        } catch (Exception e) {
            log.error("게시글 조회에 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 게시글 좋아요 클릭
    @PostMapping("/like/{boardId}")
    public ResponseEntity<ApiResponseDto<Integer>> likeBoard (@PathVariable Long boardId) {
        try {
            Integer likeCount = boardReactionService.toggleLike(boardId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "좋아요 클릭 성공", likeCount));
        } catch (Exception e) {
            log.error("오류가 발생하였습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    // 좋아요 상위 게시글 조회
    @GetMapping("/like/list")
    public ResponseEntity<ApiResponseDto<BoardLikeGroupResDto>> likeBoardList() {
        try {
            BoardLikeGroupResDto boards = boardService.bookLikeList();
            return ResponseEntity.ok(new ApiResponseDto<>(true, "좋아요 상위 게시글 조회 성공", boards));
        } catch (Exception e) {
            log.error("게시글 조회 중 오류가 발생하였습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/weekly-best")
    public ResponseEntity<ApiResponseDto<List<BoardWeeklyBestResDto>>> getWeeklyBest(
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        List<BoardWeeklyBestResDto> result = boardService.getWeeklyBest(limit);

        ApiResponseDto<List<BoardWeeklyBestResDto>> response =
                new ApiResponseDto<>(
                        true,
                        "주간 인기글 조회 성공",
                        result
                );

        return ResponseEntity.ok(response);
    }
}
