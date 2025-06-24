package org.kosa.tripTalk.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.tripTalk.product.discount.Discount;
import org.kosa.tripTalk.product.discount.DiscountType;

@DisplayName("상품 할인 가격 계산 테스트")
class ProductTest {

	@Test
    @DisplayName("할인 정보가 없으면 원래 가격 반환")
    void noDiscount() {
        Product product = Product.builder()
                .price(10000)
                .build();

        assertThat(product.getDiscountedPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("정액 할인 적용")
    void fixedDiscount() {
        Discount discount = Discount.builder()
                .discountType(DiscountType.FIXED)
                .discountAmount(2000)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(1))
                .build();

        Product product = Product.builder()
                .price(10000)
                .build();

        product.setDiscount(discount); // 연관관계 메서드 사용

        assertThat(product.getDiscountedPrice()).isEqualTo(8000);
    }

    @Test
    @DisplayName("정률 할인 적용")
    void rateDiscount() {
        Discount discount = Discount.builder()
                .discountType(DiscountType.RATE)
                .discountRate(0.1)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(1))
                .build();

        Product product = Product.builder()
                .price(20000)
                .build();

        product.setDiscount(discount);

        assertThat(product.getDiscountedPrice()).isEqualTo(18000);
    }

    @Test
    @DisplayName("할인 기간이 아니면 원래 가격 반환")
    void inactiveDiscount() {
        Discount discount = Discount.builder()
                .discountType(DiscountType.FIXED)
                .discountAmount(1000)
                .startAt(LocalDateTime.now().minusDays(10))
                .endAt(LocalDateTime.now().minusDays(5))
                .build();

        Product product = Product.builder()
                .price(15000)
                .build();

        product.setDiscount(discount);

        assertThat(product.getDiscountedPrice()).isEqualTo(15000);
    }

}
