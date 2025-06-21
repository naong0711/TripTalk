package org.kosa.tripTalk.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>{
  
  //마이페이지 예약내역 추출용
  List<Reservation> findByUserUserId(String userId);
}
