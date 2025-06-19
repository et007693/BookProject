package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_comment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "isbn"})
})
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "book"})
public class BookCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCid;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    public void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "isbn")
    private BookEntity book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
