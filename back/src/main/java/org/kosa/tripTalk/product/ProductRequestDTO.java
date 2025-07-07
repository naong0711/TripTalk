package org.kosa.tripTalk.product;

import java.time.LocalDateTime;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.product.discount.Discount;
import org.kosa.tripTalk.product.discount.DiscountDTO;
import org.kosa.tripTalk.seller.Seller;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endDate;
    
    @NotNull(message = "판매자 ID는 필수입니다.")
    private Long sellerId;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    private DiscountDTO discount;
    
    private Integer minPeople;
    private Integer maxPeople;
    
    private String location;
    


    public Product toEntity(Seller seller, Category category) {
        Product product = Product.builder()
                .title(this.title)
                .description(this.description)
                .address(this.address)
                .price(this.price)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .seller(seller)
                .category(category)
                .minPeople(this.minPeople)
                .maxPeople(this.maxPeople)
                .location(this.location)
                .build();
        

        Discount discount = DiscountDTO.toEntity(this.discount);
        if (discount != null) {
            product.applyDiscount(discount);
        }

        return product;
    }


}