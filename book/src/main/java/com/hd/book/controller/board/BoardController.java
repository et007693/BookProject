package com.hd.book.controller.board;

import com.hd.book.dto.board.BoardWriteDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "https://localhost:3000",
        "https://localhost:5173",
})
@RequestMapping("api/board")
public class BoardController {
    private final BoardService boardService;

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

    @PutMapping("/update/{boardId}")
    public ResponseEntity<ApiResponseDto<Void>> postBoard(@PathVariable Long boardId, @RequestBody BoardWriteDto dto) {
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
}
