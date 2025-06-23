package org.kosa.tripTalk.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatPayload payload,  Authentication authentication) {
      
      //헤더에서 user의 id 추출
//      User sender  = (User) authentication.getPrincipal();
//      Long senderId = sender.getId();
//      
//      // receiverId로 User 객체 조회
//      Long receiverId = payload.getReceiverId();
      
      Long senderId = (long) 2202;
      Long receiverId = (long) 1;

      //채팅방 생성
      chatService.createRoom(senderId, receiverId);
     
    }
    
    
    
    
}