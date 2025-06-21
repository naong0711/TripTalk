package org.kosa.tripTalk.cart;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartResponse {
  private Long id;
  private String title;
  private int price;

}
