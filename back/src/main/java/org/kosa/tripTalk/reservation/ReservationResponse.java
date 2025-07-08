package org.kosa.tripTalk.reservation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ReservationResponse {
  private Long id;
  private Long productId;
  private Long userId;
  private String tid; //결제고유번호
  private Long reservationDate;
  private String status;
  private Integer totalPrice;
  private String title;
  private Long sellerUserId;
  
  
  public static ReservationResponse from(Reservation r, Long sellerUserId) {
    return ReservationResponse.builder()
        .id(r.getId())
        .tid(r.getTransactionId())
        .productId(r.getProduct().getId())
        .userId(r.getUser().getId())
        .title(r.getProduct().getTitle())
        .totalPrice(r.getTotalPrice())
        .status(r.getStatus())
        .sellerUserId(sellerUserId)
        .build();
  }
}
