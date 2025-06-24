package org.kosa.tripTalk.product.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("할인 정책 테스트")
class DiscountPolicyTest {

	@Test
    @DisplayName("정액 할인: 1,000원 할인")
    void fixedDiscount() {
        DiscountPolicy policy = new FixedDiscountPolicy(1000);
        assertThat(policy.applyDiscount(10000)).isEqualTo(9000);
    }

    @Test
    @DisplayName("정률 할인: 20% 할인")
    void rateDiscount() {
        DiscountPolicy policy = new RateDiscountPolicy(0.2);
        assertThat(policy.applyDiscount(10000)).isEqualTo(8000);
    }

    @Test
    @DisplayName("할인 없음: 그대로 가격")
    void noDiscount() {
        DiscountPolicy policy = price -> price;
        assertThat(policy.applyDiscount(15000)).isEqualTo(15000);
    }

}
