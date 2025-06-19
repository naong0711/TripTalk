package org.kosa.tripTalk.payment;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final KakaoPayService kakaoPayService;

    @PostMapping("/create")
    public ResponseEntity<KakaoPayReadyResponse> create(@RequestBody PaymentRequest request) {
        // DB에 결제 준비 상태로 저장
        paymentService.createPayment(request);

        // 카카오페이 결제 요청
        KakaoPayReadyResponse response = kakaoPayService.kakaoPayReady(request);

       

        return ResponseEntity.ok(response);
    }

    @GetMapping("/approve")
    public ResponseEntity<KakaoPayApproveResponse> approve(@RequestParam("pg_token") String pgToken, @RequestParam("paymentId") Long paymentId) {
    	  // 1) 카카오페이 승인 요청
        KakaoPayApproveResponse response = kakaoPayService.kakaoPayApprove(pgToken,paymentId);
        System.out.println(response);System.out.println(response.getStatus());

        // 2) 승인 성공 시 예약 생성 처리
        if ("APPROVED".equalsIgnoreCase(response.getStatus())) { // 상태 체크, 실제 상태명은 카카오페이 API 문서 참고
            LocalDateTime approvedAt = LocalDateTime.now(); // 혹은 response에서 받은 승인 시각
            paymentService.approvePaymentAndCreateReservation(paymentId, approvedAt);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        return paymentService.getPayment(id)
                .map(payment -> ResponseEntity.ok(PaymentResponse.builder()
                        .id(payment.getId())
                        .transactionId(payment.getTransactionId())
                        .paymentMethod(payment.getPaymentMethod())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .paymentDate(payment.getPaymentDate())
                        .refundDate(payment.getRefundDate())
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}