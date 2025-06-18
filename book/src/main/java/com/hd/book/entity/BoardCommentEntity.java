package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_comment")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardCid;

    @Column(nullable = false)
    private String content;

    private Integer likeCount = 0;
    private Integer hateCount = 0;

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
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity board;
}
