package org.kosa.tripTalk.chat;

import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatPayload payload,  Authentication authentication) {
      
      //헤더에서 user의 id 추출
//      User sender  = (User) authentication.getPrincipal();
//      Long senderId = sender.getId();
//      
//      // receiverId로 User 객체 조회
      Long receiverId = payload.getReceiverId();
      
      //테스트 상 인증불가로 하드코딩
      Long senderId = (long) 2202;
      User sender = userRepository.findById(senderId) .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));
      
      System.out.println("========="+sender);
      System.out.println("========="+senderId);
//      Long receiverId = (long) 1;

      //채팅방 생성
      ChatRoom room = chatService.getOrCreateRoom(senderId, receiverId);
      
      //메시지 저장이나 전송 처리 (필요 시)
      chatService.processMessage(room, sender, payload.getMessage());
     
    }
    
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<String> getRoomId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        String roomId = chatService.generateRoomId(senderId, receiverId);
        return ResponseEntity.ok(roomId);
    }
    
    
    
    
}