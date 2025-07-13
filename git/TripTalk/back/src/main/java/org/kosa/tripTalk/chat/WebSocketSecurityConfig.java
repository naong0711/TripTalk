package org.kosa.tripTalk.chat;

import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {
  
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final HttpHandshakeInterceptor httpHandshakeInterceptor;
  
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-ws") // JS에서 연결 요청하는 엔드포인트
                .setAllowedOriginPatterns("*")
                .addInterceptors(httpHandshakeInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); //요청 경로
        registry.enableSimpleBroker("/sub"); //구독(수신) 경로
        registry.setUserDestinationPrefix("/user");
    }
    
    @Bean
    public AuthChannelInterceptor authChannelInterceptor() {
        return new AuthChannelInterceptor(jwtUtil, userService);
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
      System.out.println("✅ configureClientInboundChannel 호출됨");  
      registration.interceptors(authChannelInterceptor());
    }

}