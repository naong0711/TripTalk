package org.kosa.tripTalk.product.discount;

import lombok.RequiredArgsConstructor;

// 퍼센트 할인
@RequiredArgsConstructor
public class RateDiscountPolicy implements DiscountPolicy {

	private final double rate;
	
	@Override
	public int applyDiscount(int price) {
		return (int)(price * (1 - (rate / 100.0)));
	}

}
