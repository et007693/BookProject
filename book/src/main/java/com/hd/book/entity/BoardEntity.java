package com.hd.book.entity;

import com.hd.book.constant.BoardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BoardType boardType;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;
    private Integer likeCount = 0;

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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardCommentEntity> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "isbn", nullable = false)
    private BookEntity book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
