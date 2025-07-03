package org.kosa.tripTalk.product.discount;

import java.time.LocalDateTime;

import org.kosa.tripTalk.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Discount {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // FIXED, RATE, NONE
    
    @Column(nullable = true)
    private Integer discountAmount; // 정액 할인용 (예: 1000)
    private Double discountRate;    // 정률 할인용 (예: 0.1)

    @Column(nullable = false)
    private String name; // 예: "6월 특가"

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;
    
    // 할인 유효 기간 여부 판단
    public boolean isActive() {
    	LocalDateTime now = LocalDateTime.now();
    	return now.isAfter(startAt) && now.isBefore(endAt);
    }
    
    // 할인 타입에 따라 해당 전략 객체 직접 반환
    // 추후 정책이 복잡해질 경우 별도 Factory 클래스로 리팩토링
    public DiscountPolicy toPolicy() {
    	if (discountType == null) return price -> price;
    	
    	return switch (discountType) {
    		case FIXED -> new FixedDiscountPolicy(discountAmount);
    		case RATE -> new RateDiscountPolicy(discountRate);
    		default -> price -> price;
    	};
    }

    // 방향 연관관계를 맞추기 위해 사용
    // product 엔티티의 applyDiscount() 내부에서 호출됨
	public void setProduct(Product product) {
		this.product = product;
	}
}
