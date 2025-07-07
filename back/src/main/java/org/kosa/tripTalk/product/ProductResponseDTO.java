package org.kosa.tripTalk.product;

import java.time.LocalDateTime;

import org.kosa.tripTalk.product.discount.DiscountDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductResponseDTO {


    private Long id;
    private String title;
    private String description;
    private int price;
    private int discountedPrice; 
    private String address;

    private String categoryName;
    private Long categoryId;

    private String sellerName;
    private Long sellerId;

    private DiscountDTO discount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private Integer minPeople;
    private Integer maxPeople;
    
    private String location;

    public static ProductResponseDTO from(Product product) {
        if (product == null) return null;

        String categoryName = null;
        Long categoryId = null;
        if (product.getCategory() != null) {
            categoryName = product.getCategory().getKind();
            categoryId = product.getCategory().getId();
        }

        String sellerName = null;
        Long sellerId = null;
        if (product.getSeller() != null && product.getSeller().getUser() != null) {
            sellerName = product.getSeller().getUser().getName();
            sellerId = product.getSeller().getId();
        }

        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountedPrice(product.getDiscountedPrice()) // 여기 반드시 할인 적용된 값
                .address(product.getAddress())

                .categoryName(categoryName)
                .categoryId(categoryId)

                .sellerName(sellerName)
                .sellerId(sellerId)

                .discount(DiscountDTO.from(product.getDiscount()))

                .startDate(product.getStartDate())
                .endDate(product.getEndDate())
                .minPeople(product.getMinPeople())
                .maxPeople(product.getMaxPeople())
                .location(product.getLocation())
                .build();
    	}
	}