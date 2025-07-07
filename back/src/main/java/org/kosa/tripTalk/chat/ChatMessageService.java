package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.kosa.tripTalk.seller.Seller;
import org.kosa.tripTalk.seller.SellerRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository roomRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository; // 새로 추가
    private final SimpMessagingTemplate messagingTemplate;
    
    // userId: 구매자 User PK, sellerId: 판매자 Seller PK
    public String generateRoomId(Long userId, Long sellerId) {
        if (userId == null || sellerId == null) {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }
        return "user" + userId + "_seller" + sellerId;
    }
    
    // 새 방 생성 (private)
    private ChatRoom createRoom(Long userId, Long sellerId) {
        User customer = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("구매자 없음"));
        Seller seller = sellerRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자 없음"));
        
        String roomId = generateRoomId(userId, sellerId);

        ChatRoom room = new ChatRoom();
        room.setId(roomId);
        room.setCustomer(customer);
        room.setSeller(seller);
        room.setCreatedAt(LocalDateTime.now());

        return roomRepository.save(room);
    }

    // 방 있으면 가져오고 없으면 생성
    public ChatRoom getOrCreateRoom(Long userId, Long sellerId) {
        String roomId = generateRoomId(userId, sellerId);
        return roomRepository.findById(roomId).orElseGet(() -> createRoom(userId, sellerId));
    }

    // 메시지 저장 및 발송
    public void processMessage(ChatRoom room, User sender, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
            .room(room)
            .sender(sender)
            .message(message)
            .sentAt(LocalDateTime.now())
            .build();

        messageRepository.save(chatMessage);
        messagingTemplate.convertAndSend("/sub/chat/room/" + room.getId(),
            new ChatMessageResponse(chatMessage, sender.getId()));
    }

    // 사용자별 채팅방 리스트 조회
    public List<ChatRoomResponse> getChatRoomsByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 사용자가 판매자 권한 있으면 Seller 조회
        Optional<Seller> sellerOpt = sellerRepository.findByUser(user);

        // 구매자 역할인 방 조회
        List<ChatRoom> roomsAsBuyer = roomRepository.findByCustomer(user);

        // 판매자 역할인 방 조회
        List<ChatRoom> roomsAsSeller = sellerOpt
            .map(seller -> roomRepository.findBySeller(seller))
            .orElse(Collections.emptyList());

        // 구매자+판매자 방 통합 (중복 제거)
        List<ChatRoom> allRooms = Stream.concat(roomsAsBuyer.stream(), roomsAsSeller.stream())
            .distinct()
            .collect(Collectors.toList());

        return allRooms.stream()
            .map(room -> {
                boolean isBuyer = room.getCustomer().getId().equals(userId); //유저가 구매자인지

                User opponentUser = isBuyer ? room.getSeller().getUser() : room.getCustomer();

                Optional<ChatMessage> lastMsgOpt = messageRepository.findLatestMessageByRoomId(room.getId());
                String lastMessage = lastMsgOpt.map(ChatMessage::getMessage).orElse("메시지가 없습니다");
                LocalDateTime sentAt = lastMsgOpt.map(ChatMessage::getSentAt).orElse(null);
                
                Long lastSenderId = lastMsgOpt.map(msg -> msg.getSender().getId()).orElse(null);

                String lastMessageSenderRole = "";
                if (lastSenderId != null) {
                    if (lastSenderId.equals(room.getCustomer().getId())) {
                        lastMessageSenderRole = "BUYER";
                    } else if (lastSenderId.equals(room.getSeller().getUser().getId())) {
                        lastMessageSenderRole = "SELLER";
                    }
                }
                
                int unreadCount = messageRepository.countUnreadMessages(room.getId(), userId);

                return new ChatRoomResponse(
                    room.getId(),
                    opponentUser.getId(),
                    opponentUser.getNickname(),
                    lastMessage,
                    isBuyer ? "BUYER" : "SELLER",
                    lastMessageSenderRole,
                    sentAt,
                    unreadCount
                );
            })
            .collect(Collectors.toList());
    }
    
    //로그인 유저가 판매자인지 확인
    public boolean isSeller(User user) {
        return sellerRepository.findByUser(user).isPresent();
    }
    
    //로그인 유저의 Seller ID 조회 (없으면 예외 or null)
    public Long getSellerIdByUser(User user) {
        return sellerRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("판매자 정보가 없습니다"))
                .getId();
    }

    //채팅방 대화기록
    @Transactional
    public List<ChatMessageResponse> getMessagesByRoomId(String roomId, Long currentUserId) {
      messageRepository.markMessagesAsReadForUser(roomId, currentUserId); //읽음 여부 확인

      List<ChatMessage> messages = messageRepository.findByRoom_IdOrderBySentAtAsc(roomId); // 최신 상태로 다시 조회

      return messages.stream()
          .map(msg -> new ChatMessageResponse(msg, currentUserId))
          .collect(Collectors.toList());
  }

    public void handleIncomingMessage(ChatPayload payload, User sender) {
      
      Long senderId = sender.getId();
      String senderRole = payload.getSenderRole(); // "USER" or "SELLER"
      Long receiverId = payload.getReceiverId();

      Long userId, sellerId;

      if ("USER".equalsIgnoreCase(senderRole)) {
          userId = senderId;
          sellerId = receiverId;
          sellerRepository.findById(sellerId)
              .orElseThrow(() -> new IllegalArgumentException("판매자 없음"));
      } else if ("SELLER".equalsIgnoreCase(senderRole)) {
          sellerId = getSellerIdByUser(sender); // 이미 있는 메서드
          userId = receiverId;
          userRepository.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
      } else {
          throw new IllegalArgumentException("유효하지 않은 역할: " + senderRole);
      }

      ChatRoom room = getOrCreateRoom(userId, sellerId);
      processMessage(room, sender, payload.getMessage());
  }
    
    
}