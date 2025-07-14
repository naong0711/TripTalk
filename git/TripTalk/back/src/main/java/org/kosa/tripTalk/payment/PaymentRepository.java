package org.kosa.tripTalk.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{

  Optional<Payment> findByTransactionId(String transactionId);
    // JpaRepository를 상속하여 Payment 엔티티에 대한 CRUD 및 페이징, 정렬 기능 제공
    // 별도의 메서드 정의 없이 기본적인 DB 작업 가능

}