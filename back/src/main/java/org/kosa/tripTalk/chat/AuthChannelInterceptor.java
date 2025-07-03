package org.kosa.tripTalk.chat;

import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthChannelInterceptor(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("🟡 preSend 호출됨");

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("🟢 STOMP CONNECT 처리 중");

            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(bearerToken)) {
                System.out.println("🔑 Authorization 헤더: " + bearerToken);

                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);
                    try {
                        String userId = jwtUtil.extractUserId(token);
                        System.out.println("✅ 토큰 유효, userId = " + userId);

                        User user = userService.findByUserId(userId);
                        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        accessor.setUser(auth);

                        System.out.println("✅ WebSocket 인증 성공: " + userId);
                    } catch (Exception e) {
                        System.out.println("❌ WebSocket 인증 실패: " + e.getMessage());
                        throw new IllegalArgumentException("Invalid JWT Token in WebSocket", e);
                    }
                }
            } else {
                System.out.println("❗ Authorization 헤더 없음");
            }
        }

        return message;
    }

}
