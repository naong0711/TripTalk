package org.kosa.tripTalk.product.discount;

// 할인 정책 인터페이스
public interface DiscountPolicy {
	int applyDiscount(int price);
}
