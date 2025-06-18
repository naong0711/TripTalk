package org.kosa.tripTalk.product;

import java.time.LocalDateTime;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.seller.Seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
	private String title;
    private String description;
    private String address;
    private Integer price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long sellerId;
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
