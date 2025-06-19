package com.hd.book.entity;

import com.hd.book.constant.HistoryStatus;
import jakarta.persistence.*;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "history_book")
@ToString(exclude = {"history", "book"})
public class HistoryBookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = false)
    private HistoryEntity history;

    @ManyToOne
    @JoinColumn(name = "isbn", nullable = false)
    private BookEntity book;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HistoryStatus status;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;

    private String memo;
}
