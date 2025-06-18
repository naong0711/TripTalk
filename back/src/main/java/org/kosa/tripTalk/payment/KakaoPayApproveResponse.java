package org.kosa.tripTalk.payment;

import lombok.Getter;

@Getter
public class KakaoPayApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private Amount amount;

    @Getter
    public static class Amount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
    }
}