package org.kosa.tripTalk.payment;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import org.kosa.tripTalk.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final KakaoPayService kakaoPayService;

    // ✅ 결제 준비 (POST /api/payments/create)
    @PostMapping("/create")
    public ResponseEntity<KakaoPayReadyResponse> create(@RequestBody PaymentRequest request, Authentication authentication) {
      // 1. 인증 정보에서 사용자 추출
      User user = (User) authentication.getPrincipal();
      request.setUserId(user.getUserId()); // ✅ 먼저 userId를 설정

      // 2. DB에 결제 준비 상태로 저장 (여기서 userId 포함됨)
      paymentService.createPayment(request);

      // 3. 카카오페이 결제 요청
      KakaoPayReadyResponse response = kakaoPayService.kakaoPayReady(request);

      return ResponseEntity.ok(response);
    }

    // ✅ 결제 승인 (프론트로 리다이렉트)
    @GetMapping("/approve")
    public RedirectView approve(@RequestParam("pg_token") String pgToken,
                                 @RequestParam("paymentId") Long paymentId) {

        // 1. 카카오페이 승인 요청
        KakaoPayApproveResponse response = kakaoPayService.kakaoPayApprove(pgToken, paymentId);

        // 2. DB에서 payment를 조회해서 상태 확인
        Optional<Payment> optionalPayment = paymentService.getPayment(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();

            // 결제 상태가 APPROVED일 경우 예약 생성 및 상태 업데이트 처리
            if ("APPROVED".equalsIgnoreCase(payment.getStatus())) {
                paymentService.approvePaymentAndCreateReservation(paymentId, LocalDateTime.now());
            }
        } else {
            throw new IllegalArgumentException("해당 결제 ID가 존재하지 않습니다: " + paymentId);
        }

        // 3. 프론트의 결제 성공 페이지로 리다이렉트
        return new RedirectView("http://localhost:5173/payment/success");
    }

    // ✅ 단건 결제 조회
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable("id") Long id) {
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
    
    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody RefundRequest request) {
        try {
          System.out.println("TID = " + request.getTid());
          System.out.println("CancelAmount = " + request.getCancelAmount());

          KakaoPayRefundResponse refundResponse = kakaoPayService.kakaoPayRefund(request.getTid(), request.getCancelAmount());
          paymentService.markAsRefunded(request.getTid(), LocalDateTime.now());

          return ResponseEntity.ok(refundResponse);
        } catch (Exception e) {
          e.printStackTrace(); // 에러 자세히 출력
          return ResponseEntity.badRequest().body("환불 실패: " + e.getMessage());
        }
    }
    
    @PostMapping("/refund/test")
    public ResponseEntity<?> refundTest(@RequestBody RefundRequest request) {
          System.out.println("TID = " + request.getTid());
          System.out.println("CancelAmount = " + request.getCancelAmount());
          
          PaymentResponse paymentResponse = paymentService.refundTest(request.getTid(), request.getCancelAmount());
          
          return ResponseEntity.ok(paymentResponse);

          
    }
    
    
}