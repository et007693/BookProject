package com.hd.book.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final ChatSubscriptionInterceptor chatSubscriptionInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor,
                           ChatSubscriptionInterceptor chatSubscriptionInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
        this.chatSubscriptionInterceptor = chatSubscriptionInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1) JWT 인증 + 순수 WebSocket (Postman 테스트용)
    registry.addEndpoint("/ws-plain")
            .setAllowedOriginPatterns("*")
            .addInterceptors(jwtHandshakeInterceptor);
    // 2) 기존 SockJS 지원 엔드포인트
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .addInterceptors(jwtHandshakeInterceptor)
            .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatSubscriptionInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // prefix: 클라이언트 -> 서버로 보낼 때
        registry.setApplicationDestinationPrefixes("/app");
        // SimpleBroker: 서버 -> 클라이언트로 메시지 브로드캐스트
        registry.enableSimpleBroker("/topic", "/queue");
        // user 대상 point to point 메시징
        registry.setUserDestinationPrefix("/user");
    }
}
