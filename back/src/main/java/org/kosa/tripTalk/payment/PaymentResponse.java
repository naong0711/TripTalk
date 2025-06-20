package org.kosa.tripTalk.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private String transactionId;
    private String paymentMethod;
    private int amount;
    private String status;
    private LocalDateTime paymentDate;
    private LocalDateTime refundDate;
}