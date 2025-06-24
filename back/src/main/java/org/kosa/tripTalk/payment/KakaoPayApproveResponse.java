package org.kosa.tripTalk.payment;

import lombok.Getter;

@Getter
public class KakaoPayApproveResponse {
    private String aid;						// 요청 고유 번호 (API 호출에 대한 고유 식별자)
    private String tid;						// 결제 고유 번호 (결제 트랜잭션 ID)
    private String cid;						// 가맹점 코드 (카카오페이에서 발급한 가맹점 식별자)
    private String partner_order_id;		// 가맹점 주문 번호 (가맹점이 발급한 주문 식별자)
    private String partner_user_id;			// 가맹점 회원 ID (가맹점에서 관리하는 회원 식별자)
    private String payment_method_type;		// 결제 수단 유형 (예: CARD, MONEY 등)
    private Amount amount;					// 결제 금액 관련 상세 정보
    

    @Getter
    public static class Amount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
    }


}