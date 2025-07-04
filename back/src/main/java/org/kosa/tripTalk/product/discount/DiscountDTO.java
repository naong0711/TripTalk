package org.kosa.tripTalk.product.discount;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountDTO {
	private DiscountType discountType;
    private Integer discountAmount;
    private Double discountRate;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endAt;
    

    // DTO → Entity
    public static Discount toEntity(DiscountDTO dto) {
        if (dto == null) return null;

        boolean isAmountValid = dto.getDiscountAmount() != null && dto.getDiscountAmount() > 0;
        boolean isRateValid = dto.getDiscountRate() != null && dto.getDiscountRate() > 0;

        if (!isAmountValid && !isRateValid) {
            return null;  // 둘 다 유효하지 않으면 할인 없음 처리
        }

        return Discount.builder()
                .discountType(dto.getDiscountType())
                .discountAmount(isAmountValid ? dto.getDiscountAmount() : null)
                .discountRate(isRateValid ? dto.getDiscountRate() : null)
                .name(dto.getName())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();
    }

    // Entity → DTO
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
}