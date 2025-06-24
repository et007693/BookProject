package com.hd.book.entity;

import com.hd.book.constant.reactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "board_reaction",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "board_id"}
    )
)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "board"})
public class BoardReactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private reactionType reactionType;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
