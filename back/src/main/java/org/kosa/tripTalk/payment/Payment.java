package org.kosa.tripTalk.payment;


import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.user.User;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // 🔗 FK: 상품 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 🔗 FK: 회원 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_id", nullable = false, length = 100, unique = true)
    private String transactionId;  // 거래번호 (PG사 고유번호 등)

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;  // 결제수단 (카드, 계좌이체 등)
    
    @Column(nullable = false)
    private int amount;  // 결제 금액

    @Column(nullable = false, length = 30)
    private String status;  // 결제 상태 (SUCCESS, FAILED, REFUNDED 등)

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;  // 결제 일시

    @Column(name = "refund_date")
    private LocalDateTime refundDate;  // 환불 일시 (nullable)
    
    
}