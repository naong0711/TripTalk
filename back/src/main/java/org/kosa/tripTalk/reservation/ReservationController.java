package org.kosa.tripTalk.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

	// 예약 서비스 의존성 주입
    private final ReservationService reservationService;

    // 예약 생성 요청을 처리하는 POST API
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
    // 요청으로 받은 데이터를 이용해 예약 생성    
    	Reservation reservation = reservationService.createReservation(request);
    // 생성된 예약 객체를 HTTP 200 OK 상태와 함께 반환  
    	return ResponseEntity.ok(reservation);
    }
}