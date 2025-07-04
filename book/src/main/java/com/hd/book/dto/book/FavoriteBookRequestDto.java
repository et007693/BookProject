package com.hd.book.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBookRequestDto {
    private String isbn;
    private String title;
    private String author;
    private String cover;
}