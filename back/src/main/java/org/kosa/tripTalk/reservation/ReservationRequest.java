package org.kosa.tripTalk.reservation;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private Long userId;
    private Long productId;
    private Long reservationDate;  // 밀리초 timestamp
    private Integer totalPrice;
    
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentApprovedAt;
}