package org.kosa.tripTalk.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String userId;		// 결제 요청한 사용자 ID (로그인 ID 등)
    private Long productId;		// 결제 대상 상품 ID
    private String paymentId;	// 결제 고유 ID (결제 시스템에서 발급한 거래 ID 등)
    private String status;		// 결제 상태
    private int amount;			// 결제 금액
    private String paymentMethod;	// 결제 수단

}