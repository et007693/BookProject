package com.hd.book.service;

import com.hd.book.dto.chat.ChatMessageDto;
import com.hd.book.entity.ChatMessageEntity;
import com.hd.book.exception.ChatException;
import com.hd.book.repository.ChatMessageRepository;
import com.hd.book.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserUtil userUtil;

    // 채팅 메시지를 저장하고, 전체 구독자에게 브로드캐스트합니다.
    public void sendMessage(ChatMessageDto dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ChatException("메시지 내용이 비어 있습니다.");
        }

        dto.setTimestamp(System.currentTimeMillis());

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(dto.getSender())
                .nickname(userUtil.getUser(dto.getSender()).getNickname())
                .content(dto.getContent())
                .timestamp(dto.getTimestamp())
                .build();
        try {
            chatMessageRepository.save(entity);
        } catch (DataAccessException dae) {
            throw new ChatException("채팅 저장 중 서버 오류가 발생했습니다.", dae);
        }

        try {
            messagingTemplate.convertAndSend("/topic/chat", dto);
        } catch (MessagingException me) {
            throw new ChatException("메시지 전송 중 오류가 발생했습니다.", me);
        }
    }

    public List<ChatMessageDto> getHistory() {
        try {
            return chatMessageRepository.findAllByOrderByTimestampAsc().stream()
                    .map(e -> ChatMessageDto.builder()
                            .sender(e.getSender())
                            .nickname(e.getNickname())
                            .content(e.getContent())
                            .timestamp(e.getTimestamp())
                            .build())
                    .collect(Collectors.toList());
        } catch (DataAccessException dae) {
            throw new ChatException("채팅 기록 조회 중 서버 오류가 발생했습니다.", dae);
        }
    }
}