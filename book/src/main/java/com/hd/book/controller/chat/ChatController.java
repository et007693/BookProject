package com.hd.book.controller.chat;

import com.hd.book.dto.chat.ChatMessage;
import com.hd.book.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/join")
    @SendToUser("/queue/history")
    public List<ChatMessage> joinRoom() {
        return chatService.getHistory();
    }

    @MessageMapping("/chat/message")
    public void processMessage(ChatMessage message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }

        chatService.sendMessage(message);

    }
}
