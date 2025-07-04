package com.hd.book.service;

import com.hd.book.dto.chat.ChatMessage;
import com.hd.book.entity.ChatMessageEntity;
import com.hd.book.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(SimpMessagingTemplate messagingTemplate,
                       ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
    }

    // 채팅 메시지를 저장하고, 전체 구독자에게 브로드캐스트합니다.
    public void sendMessage(ChatMessage dto) {
        // 타임스탬프 설정
        dto.setTimestamp(System.currentTimeMillis());

        // DTO -> Entity 변환 및 저장
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(dto.getSender())
                .content(dto.getContent())
                .timestamp(dto.getTimestamp())
                .build();
        chatMessageRepository.save(entity);

        // 브로드캐스트: 전역 채팅(Room) 토픽
        messagingTemplate.convertAndSend("/topic/chat", dto);
    }

    // 전체 채팅 히스토리를 타임스탬프 순으로 조회하여 DTO 리스트로 반환합니다.

    public List<ChatMessage> getHistory() {
        return chatMessageRepository.findAllByOrderByTimestampAsc().stream()
                .map(e -> ChatMessage.builder()
                        .sender(e.getSender())
                        .content(e.getContent())
                        .timestamp(e.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
