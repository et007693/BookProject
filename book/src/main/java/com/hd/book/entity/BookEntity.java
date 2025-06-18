package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookEntity {
    @Id
    @Column(name = "isbn")
    private String isbn;

    @OneToMany(mappedBy = "book")
    private List<BoardEntity> boards = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCommentEntity> comments = new ArrayList<>();

    @ManyToMany(mappedBy = "books")
    private List<HistoryEntity> histories = new ArrayList<>();

}
