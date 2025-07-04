package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_book",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "isbn"})
        })
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = {"user", "book"})
public class FavoriteBookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", nullable = false)
    private BookEntity book;

    @Column(name = "favorited_at", nullable = false, updatable = false)
    private LocalDateTime favoritedAt;

    @PrePersist
    protected void onCreate() {
        this.favoritedAt = LocalDateTime.now();
    }

    public FavoriteBookEntity(UserEntity user, BookEntity book) {
        this.user = user;
        this.book = book;
    }
}