package org.kosa.tripTalk.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
public class KakaoPayReadyResponse {
    private String tid;							 // 결제 고유 ID (결제 준비 요청 시 발급되는 거래 ID)
    private String next_redirect_app_url;		 // 결제 완료 후 리다이렉트할 앱 URL (앱 내 결제 페이지)
    private String next_redirect_mobile_url;	 // 결제 완료 후 리다이렉트할 모바일 웹 URL
    private String next_redirect_pc_url;		 // 결제 완료 후 리다이렉트할 PC 웹 URL
    private String android_app_scheme;			 // Android 앱 스킴 URL
    private String ios_app_scheme;				 // iOS 앱 스킴 URL
    private LocalDateTime created_at;			 // 결제 준비 완료 시각
    private String status;						 // 결제 준비 상태

}