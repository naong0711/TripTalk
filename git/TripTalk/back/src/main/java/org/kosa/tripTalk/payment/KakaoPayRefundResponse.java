package org.kosa.tripTalk.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class KakaoPayRefundResponse {
    private String aid;
    private String tid;
    private String cid;
    private String status;
    private String paymentMethodType;
    private Amount amount;
    private ApprovedCancelAmount approvedCancelAmount;
    private LocalDateTime canceledAt;

    @Getter
    @Setter
    @Builder
    public static class Amount {
        private Integer total;
        private int taxFree;
        private int vat;
        private int point;
        private int discount;
        private int greenDeposit;
    }

    @Getter
    @Setter
    @Builder
    public static class ApprovedCancelAmount {
        private int total;
        private int taxFree;
        private int vat;
        private int point;
        private int discount;
        private int greenDeposit;
    }


}
