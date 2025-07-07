package com.hd.book.dto.chat;

import com.hd.book.constant.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChatMessageDto {
    private MessageType type;
    private String sender;
    private String nickname;
    private String content;
    private long timestamp;
}
