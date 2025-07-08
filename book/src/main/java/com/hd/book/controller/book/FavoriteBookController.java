package com.hd.book.controller.book;

import com.hd.book.dto.book.FavoriteBookReqDto;
import com.hd.book.dto.book.FavoriteBookResDto;
import com.hd.book.service.FavoriteBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book/favorites")
@RequiredArgsConstructor
public class FavoriteBookController {

    private final FavoriteBookService favoriteBookService;

    @PostMapping
    public ResponseEntity<FavoriteBookResDto> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FavoriteBookReqDto requestDto) {
        FavoriteBookResDto response = favoriteBookService.addFavoriteBook(
                userDetails.getUsername(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteBookResDto>> getFavoriteBooks(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FavoriteBookResDto> favoriteBooks = favoriteBookService.getFavoriteBooks(
                userDetails.getUsername());
        return ResponseEntity.ok(favoriteBooks);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String isbn) {
        favoriteBookService.removeFavoriteBook(userDetails.getUsername(), isbn);
        return ResponseEntity.noContent().build();
    }
}