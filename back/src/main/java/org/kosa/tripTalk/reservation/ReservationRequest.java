package org.kosa.tripTalk.reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private Long userId;
    private Long productId;
    private Long reservationDate;  // 밀리초 timestamp
    private Integer totalPrice;
}