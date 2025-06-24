package org.kosa.tripTalk.product.discount;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DiscountDTO {
	private DiscountType discountType;
    private Integer discountAmount;
    private Double discountRate;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    
    // dto 변환
    public static DiscountDTO from(Discount discount) {
        if (discount == null) return null;

        return DiscountDTO.builder()
                .discountType(discount.getDiscountType())
                .discountAmount(discount.getDiscountAmount())
                .discountRate(discount.getDiscountRate())
                .name(discount.getName())
                .startAt(discount.getStartAt())
                .endAt(discount.getEndAt())
                .build();
    }
    
    // 엔티티 변환
    public static Discount toEntity(DiscountDTO dto) {
        if (dto == null) return null;

        return Discount.builder()
                .discountType(dto.getDiscountType())
                .discountAmount(dto.getDiscountAmount())
                .discountRate(dto.getDiscountRate())
                .name(dto.getName())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();
    }
}
