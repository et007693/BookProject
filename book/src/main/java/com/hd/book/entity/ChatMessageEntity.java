package com.hd.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메시지 발신자(사용자 닉네임)
    @Column(name = "sender", nullable = false)
    private String sender;

    // 메시지 내용
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // UNIX epoch milliseconds
    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
}
