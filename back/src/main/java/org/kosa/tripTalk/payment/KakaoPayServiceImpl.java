package org.kosa.tripTalk.payment;



import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.reservation.ReservationRequest;
import org.kosa.tripTalk.reservation.ReservationService;
import org.kosa.tripTalk.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPayServiceImpl implements KakaoPayService {

    @Value("${kakao.secret-key}")
    private String secretKey;

    private static final String HOST = "https://open-api.kakaopay.com"; // 카카오페이 API 기본 URL
    private KakaoPayReadyResponse kakaoPayReadyResponse;
    private final PaymentService paymentService;
    private Payment Payment;

    @Override
    public KakaoPayReadyResponse kakaoPayReady(PaymentRequest request) {
        RestTemplate rt = new RestTemplate();

        // 1. Payment DB 저장
        this.Payment = paymentService.createPayment(request);
        Long paymentId = this.Payment.getId();

        // 2. approval_url에 paymentId 포함
        String approvalUrl = "http://localhost:8080/api/payments/approve?paymentId=" + paymentId;

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);
    
        // 카카오페이 결제 승인 요청용 파라미터
        Map<String, Object> payload = new HashMap<>();
        payload.put("cid", "TC0ONETIME");
        payload.put("partner_order_id", request.getProductId().toString());
        payload.put("partner_user_id", request.getUserId());
        payload.put("item_name", "여행상품");
        payload.put("quantity", 1);
        payload.put("total_amount", request.getAmount());
        payload.put("vat_amount", 0);
        payload.put("tax_free_amount", 0);
        payload.put("approval_url", approvalUrl);
        payload.put("cancel_url", "http://localhost:8080/api/payments/cancel");
        payload.put("fail_url", "http://localhost:8080/api/payments/fail");

        HttpEntity<Map<String,Object>> body = new HttpEntity<>(payload, headers);

     // 카카오페이 결제 준비 API 호출 (POST)
        ResponseEntity<KakaoPayReadyResponse> res = rt.postForEntity(
            HOST + "/online/v1/payment/ready", body, KakaoPayReadyResponse.class
        );

     // 응답에서 결제 준비 정보 저장 (tid 등)
        this.kakaoPayReadyResponse = res.getBody(); // tid 저장용
        return this.kakaoPayReadyResponse;
    }

    @Override
    // DB에서 결제 정보 조회
    public KakaoPayApproveResponse kakaoPayApprove(String pgToken, Long paymentId) {
        RestTemplate rt = new RestTemplate();
        Payment payment = paymentService.getPayment(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));

     // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

     // 카카오페이 결제 승인 요청용 파라미터
        Map<String, Object> payload = new HashMap<>();
        payload.put("cid", "TC0ONETIME");
        payload.put("tid", kakaoPayReadyResponse.getTid());
        payload.put("partner_order_id", payment.getProduct().getId());
        payload.put("partner_user_id", payment.getUser().getUserId());
        payload.put("pg_token", pgToken);

        HttpEntity<Map<String,Object>> body = new HttpEntity<>(payload, headers);
    
     // 카카오페이 결제 승인 API 호출 (POST)
        ResponseEntity<KakaoPayApproveResponse> res = rt.postForEntity(
            HOST + "/online/v1/payment/approve", body, KakaoPayApproveResponse.class);

        return res.getBody();
    }

    @Override
    public KakaoPayRefundResponse kakaoPayRefund(String tid, Integer cancelAmount) {
        RestTemplate rt = new RestTemplate();

        // 🔍 DB에서 결제 정보 확인 (transactionId == tid)
        Payment payment = paymentService.getPaymentByTransactionId(tid)
            .orElseThrow(() -> new IllegalArgumentException("해당 거래 ID에 대한 결제 내역이 없습니다."));

        // ✅ 상태 확인
        if (!"APPROVED".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("이미 환불되었거나 승인되지 않은 거래입니다.");
        }
        if (cancelAmount > payment.getAmount()) {
            throw new IllegalArgumentException("환불 금액이 결제 금액보다 클 수 없습니다.");
        }

        // 📡 HTTP 요청 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + secretKey);
        System.out.println("🔍 Authorization Header: " + headers.getFirst("Authorization"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("cancel_amount", cancelAmount.toString());
        params.add("cancel_tax_free_amount", "0");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoPayRefundResponse> response = rt.postForEntity(
            HOST + "/v1/payment/cancel",
            request,
            KakaoPayRefundResponse.class
        );

        // 💾 DB 업데이트
        paymentService.markAsRefunded(tid, LocalDateTime.now());

        return response.getBody();
    }

}