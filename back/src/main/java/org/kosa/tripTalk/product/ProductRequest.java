package org.kosa.tripTalk.product;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
	private String title;
    private String description;
    private String address;
    private Integer price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long sellerId;
    private Long categoryId;
}
