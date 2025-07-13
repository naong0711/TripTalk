package org.kosa.tripTalk.payment;

public interface KakaoPayService {
	
    KakaoPayReadyResponse kakaoPayReady(PaymentRequest request);
	KakaoPayApproveResponse kakaoPayApprove(String pgToken, Long paymentId);
    KakaoPayRefundResponse kakaoPayRefund(String tid, Integer cancelAmount);

}