package org.kosa.tripTalk.product.discount;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DiscountType {
	FIXED("정액 할인"),   // 예: 1,000원 할인
    RATE("정률 할인"),    // 예: 10% 할인
    NONE("할인 없음");    // 할인 미적용
	
	  private final String label;
}
