package com.hd.book.dto.book;

import com.hd.book.entity.FavoriteBookEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBookResponseDto {
    private Long favoriteId;
    private String isbn;
    private String title;
    private String author;
    private String cover;
    private Long userId;
    private LocalDateTime favoritedAt;

    public FavoriteBookResponseDto(FavoriteBookEntity favoriteBookEntity) {
        this.favoriteId = favoriteBookEntity.getFavoriteId();
        this.isbn = favoriteBookEntity.getBook().getIsbn();
        this.title = favoriteBookEntity.getBook().getTitle();
        this.author = favoriteBookEntity.getBook().getAuthor();
        this.cover = favoriteBookEntity.getBook().getCover();
        this.userId = favoriteBookEntity.getUser().getUserId();
        this.favoritedAt = favoriteBookEntity.getFavoritedAt();
    }
}