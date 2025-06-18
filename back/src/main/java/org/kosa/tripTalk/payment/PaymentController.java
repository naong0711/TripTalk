package org.kosa.tripTalk.payment;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<KakaoPayApproveResponse> approve(@RequestParam("pg_token") String pgToken) {
        KakaoPayApproveResponse response = kakaoPayService.kakaoPayApprove(pgToken);

        // 결제 승인 이후 상태 업데이트 등은 필요시 여기에 추가
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