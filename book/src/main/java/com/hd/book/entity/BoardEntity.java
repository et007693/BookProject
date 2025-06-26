package com.hd.book.entity;

import com.hd.book.constant.BoardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "book", "comments"})
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BoardType type;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    private Integer likeCount = 0;

    private Boolean isLiked;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardCommentEntity> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "isbn", nullable = false)
    private BookEntity book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardReactionEntity> reaction = new ArrayList<>();
}
