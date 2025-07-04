package org.kosa.tripTalk.product.discount;

import lombok.RequiredArgsConstructor;

// 퍼센트 할인
@RequiredArgsConstructor
public class RateDiscountPolicy implements DiscountPolicy {

	private final double rate;
	
	// rate 값이 0.1이라면 → 10% 할인
	@Override
	public int applyDiscount(int price) {
	    return (int)(price * (1 - rate)); // ✅ 수정된 부분
	}

}
