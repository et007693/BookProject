package com.hd.book.controller.book;

import com.hd.book.dto.book.BestSellerResDto;
import com.hd.book.dto.book.BookRatingResDto;
import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/best")
    public ResponseEntity<BestSellerResDto> getBestSellerData() {
        BestSellerResDto response = bookService.getBestSeller();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{isbn}/rating")
    public ResponseEntity<ApiResponseDto<BookRatingResDto>> getBookRating(
            @PathVariable String isbn) {
        try {
            BookRatingResDto ratingStatistics = bookService.getBookRatingStatistics(isbn);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "별점 조회에 성공하였습니다.", ratingStatistics));
        } catch (Exception e) {
            log.error("책 별점 조회에 실패했습니다. ISBN: {}, 에러: {}", isbn, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}