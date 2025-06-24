package org.kosa.tripTalk.payment;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public ResponseEntity<KakaoPayApproveResponse> approve(
            @RequestParam("pg_token") String pgToken,
            @RequestParam("paymentId") Long paymentId) {

        System.out.println(pgToken);
        System.out.println(paymentId);

     // 1) 카카오페이 승인 요청
        KakaoPayApproveResponse response = kakaoPayService.kakaoPayApprove(pgToken, paymentId);
        System.out.println(response);

        // 2) DB에서 payment를 조회해서 상태 확인
        Optional<Payment> optionalPayment = paymentService.getPayment(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            System.out.println("DB Status = " + payment.getStatus());

         // 결제 상태가 APPROVED일 경우 예약 생성 및 상태 업데이트 처리
            if ("APPROVED".equalsIgnoreCase(payment.getStatus())) {
                LocalDateTime approvedAt = LocalDateTime.now(); // 혹은 response에서 승인 시간 가져올 수도 있음
                paymentService.approvePaymentAndCreateReservation(paymentId, approvedAt);
            }
        } else {
        	// 결제 ID가 DB에 존재하지 않을 경우 예외 처리
            throw new IllegalArgumentException("해당 결제 ID가 존재하지 않습니다: " + paymentId);
        }

        return ResponseEntity.ok(response);
    }
    
    // 3) 결제 정보 단건 조회 (GET /payments/{id})
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
                .orElse(ResponseEntity.notFound().build());	// 해당 결제 건이 없으면 404 반환
    }
}