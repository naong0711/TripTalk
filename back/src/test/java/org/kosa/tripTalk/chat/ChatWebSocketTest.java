package org.kosa.tripTalk.chat;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatWebSocketTest {

    private static final String WEBSOCKET_URI = "ws://localhost:8080/ws/websocket";
    private static final String SUBSCRIBE_ENDPOINT = "/sub/chat/room/1";
    private static final String SEND_ENDPOINT = "/pub/chat/message";

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testSendAndReceiveChatMessage() throws Exception {
        CompletableFuture<ChatPayload> completableFuture = new CompletableFuture<>();

        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe(SUBSCRIBE_ENDPOINT, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatPayload.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        completableFuture.complete((ChatPayload) payload);
                    }
                });

                ChatPayload message = new ChatPayload("1", "testUser", "테스트 메시지입니다");
                session.send(SEND_ENDPOINT, message);
            }
        };

        ListenableFuture<StompSession> connectFuture = stompClient.connect(WEBSOCKET_URI, handler);
        StompSession session = connectFuture.get(3, TimeUnit.SECONDS);

        ChatPayload received = completableFuture.get(5, TimeUnit.SECONDS);
        System.out.println("🔔 수신된 메시지: " + received);

        session.disconnect();
    }
}