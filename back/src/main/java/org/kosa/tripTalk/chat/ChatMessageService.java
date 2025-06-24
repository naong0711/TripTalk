package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository messageRepository;
  private final ChatRoomRepository roomRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;
  
    //userId 비교해서 작은Id_큰id 형태로 roomId 생성
    public String generateRoomId(Long customerId, Long sellerId) {
      if (customerId == null || sellerId == null) {
          throw new IllegalArgumentException("사용자 없음");
      }
      return (customerId < sellerId) 
              ? customerId + "_" + sellerId 
              : sellerId + "_" + sellerId;
    }
    
    // 새 방 생성: 방이 없을 때만 호출 (private)
    private ChatRoom createRoom(Long customerId, Long sellerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("수신자 없음"));

        String roomId = generateRoomId(customerId, sellerId);

        ChatRoom room = new ChatRoom();
        room.setId(roomId);
        room.setCustomer(customer);
        room.setSeller(seller);
        room.setCreatedAt(LocalDateTime.now());

        return roomRepository.save(room);
    }

    // 방이 있으면 가져오고 없으면 생성 후 반환 (public)
    public ChatRoom getOrCreateRoom(Long customerId, Long sellerId) {
        String roomId = generateRoomId(customerId, sellerId);
        return roomRepository.findById(roomId).orElseGet(() -> createRoom(customerId, sellerId));
    }
    
    //메세지 저장
    public void processMessage(ChatRoom room, User sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoom(room);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setSentAt(LocalDateTime.now());
        messageRepository.save(chatMessage);
        
        messagingTemplate.convertAndSend("/sub/chat/room/" +room.getId(), new ChatMessageResponse(chatMessage));
        
    }
    
  
  
}
