package org.kosa.tripTalk.reservation;

import lombok.RequiredArgsConstructor;
import org.kosa.tripTalk.chat.ChatMessage;
import org.kosa.tripTalk.chat.ChatMessageRepository;
import org.kosa.tripTalk.chat.ChatMessageService;
import org.kosa.tripTalk.chat.ChatRoom;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.product.ProductRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    private final ChatMessageService chatRoomService; // 채팅방 생성 서비스
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송용
    private final ChatMessageRepository messageRepository; //메세지 저장용

    // 예약 생성 메서드
    public Reservation createReservation(ReservationRequest request) {
    	// 요청에서 전달된 userId로 사용자 조회 (없으면 예외 발생)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // 요청에서 전달된 productId로 상품 조회 (없으면 예외 발생)
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // 조회한 사용자와 상품 정보를 기반으로 예약 엔티티 생성
        Reservation reservation = Reservation.builder()
        	    .user(user)
        	    .product(product)
        	    .reservationDate(request.getReservationDate())
        	    .totalPrice(request.getTotalPrice())
        	    .status("예약완료")
        	    .paymentMethod(request.getPaymentMethod())
        	    .transactionId(request.getTransactionId())
        	    .paymentApprovedAt(request.getPaymentApprovedAt())
        	    .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // 예약 완료 후 자동 채팅방 처리 메서드 호출
        sendReservationCompleteMessage(savedReservation);

        return savedReservation;
    }
    
    //예약 후 예약확인 메세지 전송
    private void sendReservationCompleteMessage(Reservation reservation) {
      Long buyerId = reservation.getUser().getId();
      Long sellerId = reservation.getProduct().getSeller().getId();

      // 1) 채팅방 조회 또는 생성
      ChatRoom chatRoom = chatRoomService.getOrCreateRoom(buyerId, sellerId);

      // 2) 예약 완료 자동 메시지 생성 및 저장
      ChatMessage autoMsg = ChatMessage.builder()
          .room(chatRoom)
          .sender(reservation.getProduct().getSeller().getUser()) // 판매자 User 객체
          .message(String.format(
              "[시스템] 안녕하세요, %s님!\n" +
              "상품 \"%s\"에 대한 예약이 성공적으로 완료되었습니다.\n" +
              "예약번호: %d\n" +
              "예약일: %s\n\n" +
              "감사합니다. 즐거운 여행 되세요!",
              reservation.getUser().getName(),              // 예약자 닉네임
              reservation.getProduct().getTitle(),               // 상품명
              reservation.getId(),                               // 예약번호
              reservation.getReservationDate().toString()       // 예약일
          ))
          .isRead(false)
          .build();

      messageRepository.save(autoMsg);

      // 3) WebSocket으로 실시간 메시지 전송 (채팅방 구독자 전송)
      messagingTemplate.convertAndSend(
              "/sub/chat/room/" + chatRoom.getId(),
              autoMsg.toResponse(buyerId)  // buyerId 혹은 현재 접속 유저 ID로 전달
      );
  }
    
    
}
    