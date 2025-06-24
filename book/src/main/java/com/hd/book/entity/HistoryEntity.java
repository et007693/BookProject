package com.hd.book.entity;

import com.hd.book.constant.HistoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "history")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "book"})
@Builder
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HistoryStatus status;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;

    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn")
    private BookEntity book;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
