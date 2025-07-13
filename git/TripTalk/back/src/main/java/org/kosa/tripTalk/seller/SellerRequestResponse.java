package org.kosa.tripTalk.seller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellerRequestResponse {
    private Long id;            // 신청 ID
    private Long sellerId;      // 판매자 PK
    private String sellerName;  // 판매자 이름
    private String fileName;    // 첨부 파일 이름
    private Boolean status;      // 상태
}