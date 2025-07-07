package com.hd.book.dto.chat;

import com.hd.book.constant.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChatMessage {
    private MessageType type;
    private String sender;
    private String content;
    private long timestamp;
}
