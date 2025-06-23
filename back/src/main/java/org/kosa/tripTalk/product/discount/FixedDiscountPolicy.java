package org.kosa.tripTalk.product.discount;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixedDiscountPolicy implements DiscountPolicy {
	
	private final int discountAmount;

	@Override
	public int applyDiscount(int price) {
		return price -discountAmount;
	}
}
