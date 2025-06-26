package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Getter
@Setter
public class RefreshTokenEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "token", unique = true, nullable = false)
    private String token;;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;
}
