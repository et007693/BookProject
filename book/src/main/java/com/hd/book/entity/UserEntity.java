package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
@Getter @Setter
@NoArgsConstructor

public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoryEntity> histories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BoardEntity> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BoardCommentEntity> boardComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookCommentEntity> bookComments = new ArrayList<>();
}
