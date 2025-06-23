package org.kosa.tripTalk.product.discount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Discount → DiscountPolicy 변환 테스트")
class DiscountTest {

	@Test
    @DisplayName("정액 할인 전략 반환 확인")
    void toPolicy_fixed() {
        Discount discount = Discount.builder()
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .build();

        DiscountPolicy policy = discount.toPolicy();
        int discounted = policy.applyDiscount(10000);

        assertThat(discounted).isEqualTo(9000);
    }

    @Test
    @DisplayName("정률 할인 전략 반환 확인")
    void toPolicy_rate() {
        Discount discount = Discount.builder()
                .discountType(DiscountType.RATE)
                .discountRate(0.2)
                .build();

        DiscountPolicy policy = discount.toPolicy();
        int discounted = policy.applyDiscount(10000);

        assertThat(discounted).isEqualTo(8000);
    }

    @Test
    @DisplayName("할인 없음 처리")
    void toPolicy_none() {
        Discount discount = Discount.builder()
                .discountType(DiscountType.NONE)
                .build();

        DiscountPolicy policy = discount.toPolicy();
        int discounted = policy.applyDiscount(10000);

        assertThat(discounted).isEqualTo(10000);
    }

}
