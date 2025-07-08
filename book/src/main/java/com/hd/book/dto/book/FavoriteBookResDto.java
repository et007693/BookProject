package com.hd.book.dto.book;

import com.hd.book.entity.FavoriteBookEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBookResDto {
    private Long favoriteId;
    private String isbn;
    private String title;

    public FavoriteBookResDto(FavoriteBookEntity favoriteBookEntity) {
        this.favoriteId = favoriteBookEntity.getFavoriteId();
        this.isbn = favoriteBookEntity.getBook().getIsbn();
        this.title = favoriteBookEntity.getBook().getTitle();
    }
}