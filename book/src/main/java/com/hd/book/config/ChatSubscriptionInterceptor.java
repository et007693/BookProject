package com.hd.book.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSubscriptionInterceptor implements ChannelInterceptor {

    // 키: destination (/topic/chat), 값: 해당 방에 속한 세션 ID 집합
    private final Map<String, Set<String>> roomSessions = new ConcurrentHashMap<>();
    private static final int MAX_USERS_PER_ROOM = 100; // 인원 제한 100명

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && SimpMessageType.SUBSCRIBE.equals(accessor.getMessageType())) {
            String destination = accessor.getDestination();
            String sessionId = accessor.getSessionId();
            if (destination != null && destination.startsWith("/topic/chat")) {
                // 현재 방의 세션 집합 확보
                Set<String> sessions = roomSessions.computeIfAbsent(destination, key -> ConcurrentHashMap.newKeySet());
                if (!sessions.contains(sessionId) && sessions.size() >= MAX_USERS_PER_ROOM) {
                    // 방이 가득 찼으므로 구독 차단
                    throw new IllegalStateException("Chat room is full: " + destination);
                }
                // 세션 추가
                sessions.add(sessionId);
            }
        } else if (accessor != null && (
            SimpMessageType.UNSUBSCRIBE.equals(accessor.getMessageType()) ||
            SimpMessageType.DISCONNECT.equals(accessor.getMessageType()))) {
            String sessionId = accessor.getSessionId();
            // 모든 방에서 세션 제거
            roomSessions.values().forEach(sessions -> sessions.remove(sessionId));
        }
        return message;
    }
}
