package org.kosa.tripTalk.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;					 // 결제 식별자 (Primary Key)
    private String transactionId;		 // 거래 고유 식별자 (UUID 등으로 생성)
    private String paymentMethod;		 // 결제 방식 (예: 카드, 카카오페이 등)
    private int amount;					 // 결제 금액
    private String status;				 // 결제 상태 (예: APPROVED, CANCELLED 등)
    private LocalDateTime paymentDate;	 // 결제가 완료된 일시
    private LocalDateTime refundDate;	 // 환불된 일시 (환불이 있을 경우만 값이 존재)
    
    public static PaymentResponse from(Payment payment) {
      if (payment == null) {
          return null;
      }
      
      return PaymentResponse.builder()
          .id(payment.getId())
          .transactionId(payment.getTransactionId())
          .paymentMethod(payment.getPaymentMethod())
          .amount(payment.getAmount())
          .status(payment.getStatus())
          .paymentDate(payment.getPaymentDate())
          .refundDate(payment.getRefundDate())
          .build();
  }
}