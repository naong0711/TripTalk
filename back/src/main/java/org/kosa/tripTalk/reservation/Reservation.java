package org.kosa.tripTalk.reservation;
import jakarta.persistence.*;
import java.time.LocalDate;

import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.user.User;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "reservation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // 사용자 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 상품 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "reservation_date", nullable = false)
    private Long reservationDate;  // 예약 일시 (NUMBER → timestamp millis 등)


    @Column(name = "status", nullable = false, length = 30)
    private String status;  // 예약 상태 (예: 예약완료, 취소됨 등)

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;  // 총 가격

}