package org.kosa.tripTalk.product.discount;

import lombok.RequiredArgsConstructor;

// 고정할인
@RequiredArgsConstructor
public class FixedDiscountPolicy implements DiscountPolicy {
	
	private final int discountAmount;

	@Override
	public int applyDiscount(int price) {
		return price -discountAmount;
	}
}
