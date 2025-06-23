package org.kosa.tripTalk.product.discount;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateDiscountPolicy implements DiscountPolicy {

	private final double rate;
	
	@Override
	public int applyDiscount(int price) {
		return (int)(price * (1 -rate));
	}

}
