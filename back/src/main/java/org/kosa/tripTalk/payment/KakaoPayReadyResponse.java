package org.kosa.tripTalk.payment;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class KakaoPayReadyResponse {
    private String tid;
    private String next_redirect_app_url;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    private String android_app_scheme;
    private String ios_app_scheme;
    private LocalDateTime created_at;
}