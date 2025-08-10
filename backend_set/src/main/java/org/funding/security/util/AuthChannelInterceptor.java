package org.funding.security.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtProcessor jwtProcessor; // 기존 REST AuthInterceptor에서도 쓰던 거 그대로 사용

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // STOMP CONNECT 프레임에서 Authorization 헤더 읽기
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtProcessor.validateToken(token)) {
                    Long userId = jwtProcessor.getUserId(token);

                    // Principal 설정 (메시지 핸들러에서 Principal로 접근 가능)
                    accessor.setUser(
                            new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of())
                    );
                } else {
                    throw new IllegalArgumentException("Invalid JWT Token");
                }
            }
        }

        return message;
    }
}
