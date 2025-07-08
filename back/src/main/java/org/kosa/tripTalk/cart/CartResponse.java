package org.kosa.tripTalk.cart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartResponse {
  private Long id;
  private String title;
  private int price;
  private Long categoryId;
  private LocalDate startDate;
  private LocalDate endDate;

}
