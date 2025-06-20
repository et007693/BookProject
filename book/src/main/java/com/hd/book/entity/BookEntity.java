package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"boards", "comments", "histories"})
public class BookEntity {
    @Id
    @Column(name = "isbn")
    private String isbn;

    @OneToMany(mappedBy = "book")
    private List<BoardEntity> boards = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoryEntity> histories = new ArrayList<>();
}
