package org.kosa.tripTalk.chat;

import java.util.List;
import java.util.Map;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.reservation.ReservationRequest;
import org.kosa.tripTalk.reservation.ReservationService;
import org.kosa.tripTalk.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ReservationService reservationService;

    //테스트
    @PostMapping("/test-reservation")
    public ResponseEntity<Reservation> createTestReservation(@RequestBody ReservationRequest request) {
        // 결제 관련 검증 없이 바로 예약 생성
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    
    //채팅 메시지 수신 및 처리 (웹소켓 메시지 핸들링)
    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatPayload payload,  Authentication authentication) {
      User sender = (User) authentication.getPrincipal();
      chatMessageService.handleIncomingMessage(payload, sender);
  }
    
    //상대와의 채팅방 ID 조회
    @GetMapping("room")
    public ResponseEntity<String> getRoomId(@RequestParam("opponentId") Long opponentId, Authentication authentication) {
      
      User currentUser = (User) authentication.getPrincipal();
      Long currentUserUserId = currentUser.getId();
      Long currentUserSellerId = chatMessageService.getSellerIdByUserId(currentUserUserId);

      boolean currentUserIsSeller = chatMessageService.isUserSeller(currentUserUserId);
      boolean opponentIsSeller = chatMessageService.isUserSeller(opponentId);

      Long userId;   // 구매자 User PK (User 테이블 PK)
      Long sellerId; // 판매자 Seller PK (Seller 테이블 PK)

      if (currentUserIsSeller && !opponentIsSeller) {
          // 현재 로그인 유저가 판매자 → 상대는 구매자
          userId = opponentId;                       // 상대방 User PK
          sellerId = currentUserSellerId;            // 로그인 유저 Seller PK
      } else if (!currentUserIsSeller && opponentIsSeller) {
          // 현재 로그인 유저가 구매자 → 상대는 판매자
          userId = currentUserUserId;                 // 로그인 유저 User PK
          sellerId = chatMessageService.getSellerIdByUserId(opponentId);  // 상대방 Seller PK
      } else {
          throw new IllegalArgumentException("유효하지 않은 사용자 조합입니다.");
      }

      String roomId = chatMessageService.generateRoomId(userId, sellerId);

      return ResponseEntity.ok(roomId);
  }
    
    @GetMapping("room/{sellerId}")
    public ResponseEntity<String> getRoomId2(@PathVariable("sellerId") Long sellerId, Authentication authentication) {
      
      User user = (User) authentication.getPrincipal();
      Long userId = user.getId();
      
      String roomId = chatMessageService.generateRoomId(userId, sellerId);
      System.out.println("+++++++++++++" + roomId);
      
      return ResponseEntity.ok(roomId);
    }
    
    //채팅리스트 출력
    @GetMapping("rooms")
    public ResponseEntity<?> getChatRooms(Authentication authentication) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken auth) || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "인증이 필요합니다."));
        }

        User user = (User) auth.getPrincipal();
        List<ChatRoomResponse> rooms = chatMessageService.getChatRoomsByUser(user.getId());
        return ResponseEntity.ok(rooms);
    }
    
    //채팅방 로그
    @GetMapping("room/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable("roomId") String roomId, Authentication authentication) {
      try {
        User user = (User) authentication.getPrincipal();
        Long currentUserId = user.getId();
        
        System.out.println("================"+currentUserId);
        
        List<ChatMessageResponse> messages = chatMessageService.getMessagesByRoomId(roomId, currentUserId);
        return ResponseEntity.ok(messages);
    } catch (Exception e) {
        e.printStackTrace();  // 서버 콘솔에 스택트레이스 출력
        return ResponseEntity.status(500).body("서버 오류 발생: " + e.getMessage());
    }
    }
    
    
    
    
}

