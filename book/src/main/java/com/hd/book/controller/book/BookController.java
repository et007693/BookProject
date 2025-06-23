package com.hd.book.controller.book;

import com.hd.book.dto.book.BestSellerResDto;
import com.hd.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/best")
    public ResponseEntity<BestSellerResDto> getData() {
        BestSellerResDto response = bookService.getBestSeller();
        return ResponseEntity.ok(response);
    }
}
