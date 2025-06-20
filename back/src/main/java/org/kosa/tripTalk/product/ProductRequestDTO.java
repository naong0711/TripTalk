package org.kosa.tripTalk.product;

import java.time.LocalDateTime;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.seller.Seller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
	
	@NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 1000, message = "가격은 최소 1000원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDateTime endDate;

    @NotNull(message = "판매자 ID는 필수입니다.")
    private Long sellerId;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;
    
    public Product toEntity(Seller seller, Category category) {
        return Product.builder()
                .title(this.title)
                .description(this.description)
                .address(this.address)
                .price(this.price)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .seller(seller)
                .category(category)
                .build();
    }
}
