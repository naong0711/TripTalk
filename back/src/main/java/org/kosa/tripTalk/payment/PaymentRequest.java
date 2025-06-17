package org.kosa.tripTalk.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String userId;
    private Long productId;
    private int amount;
    private String paymentMethod;
}