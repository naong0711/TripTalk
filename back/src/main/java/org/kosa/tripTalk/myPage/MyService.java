package org.kosa.tripTalk.myPage;

import java.util.List;
import java.util.stream.Collectors;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.reservation.ReservationRepository;
import org.kosa.tripTalk.reservation.ReservationResponse;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyService {
  
  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;
  
  
  public ProfileResponse getProfile(String userId) {
    
    //userId로 사용자 조회
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    return ProfileResponse.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .phone(user.getPhone())
        .build();
  }


  public List<ReservationResponse> reservationList(String userId) {

    List<Reservation> reservations = reservationRepository.findByUserUserId(userId);
    
    return reservations.stream()
    .map(reservation -> ReservationResponse.builder()
        .id(reservation.getId())
        .reservationDate(reservation.getReservationDate())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .title(reservation.getProduct().getTitle())
        .build())
    .collect(Collectors.toList());

  }

  
}