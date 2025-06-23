package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatMessageRepository messageRepository;
  private final ChatRoomRepository roomRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;
  
    //userId 비교해서 작은Id_큰id 형태로 roomId 생성
    public String generateRoomId(Long userId1, Long userId2) {
      if (userId1 == null || userId2 == null) {
          throw new IllegalArgumentException("사용자 없음");
      }
      return (userId1 < userId2) 
              ? userId1 + "_" + userId2 
              : userId2 + "_" + userId1;
    }
    
    // 새 방 생성: 방이 없을 때만 호출 (private)
    private ChatRoom createRoom(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new IllegalArgumentException("수신자 없음"));

        String roomId = generateRoomId(senderId, receiverId);

        ChatRoom room = new ChatRoom();
        room.setId(roomId);
        room.setUser1(sender);
        room.setUser2(receiver);
        room.setCreatedAt(LocalDateTime.now());

        return roomRepository.save(room);
    }

    // 방이 있으면 가져오고 없으면 생성 후 반환 (public)
    public ChatRoom getOrCreateRoom(Long senderId, Long receiverId) {
        String roomId = generateRoomId(senderId, receiverId);
        return roomRepository.findById(roomId).orElseGet(() -> createRoom(senderId, receiverId));
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
