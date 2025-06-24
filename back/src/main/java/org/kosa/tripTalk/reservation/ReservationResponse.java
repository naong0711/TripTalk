package org.kosa.tripTalk.reservation;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {
  private Long id;
  private Long reservationDate;
  private String status;
  private Integer totalPrice;
  private String title;
}
