package org.kosa.tripTalk.payment;



import java.util.HashMap;
import java.util.Map;

import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPayServiceImpl implements KakaoPayService {

    @Value("${kakao.secret-key}")
    private String secretKey;

    private static final String HOST = "https://open-api.kakaopay.com"; // oh note
    private KakaoPayReadyResponse kakaoPayReadyResponse;
    private Payment Payment;

    @Override
    public KakaoPayReadyResponse kakaoPayReady(PaymentRequest request) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

        Map<String, Object> payload = new HashMap<>();
        payload.put("cid", "TC0ONETIME");
        payload.put("partner_order_id", request.getProductId().toString());
        payload.put("partner_user_id", request.getUserId());
        payload.put("item_name", "여행상품");
        payload.put("quantity", 1);
        payload.put("total_amount", request.getAmount());
        payload.put("vat_amount", 0);
        payload.put("tax_free_amount", 0);
        payload.put("approval_url", "http://localhost:8080/payments/approve");
        payload.put("cancel_url", "http://localhost:8080/payments/cancel");
        payload.put("fail_url", "http://localhost:8080/payments/fail");

        HttpEntity<Map<String,Object>> body = new HttpEntity<>(payload, headers);

        ResponseEntity<KakaoPayReadyResponse> res = rt.postForEntity(
            HOST + "/online/v1/payment/ready", body, KakaoPayReadyResponse.class);
        
        this.kakaoPayReadyResponse = res.getBody();

        this.Payment = new Payment();
        this.Payment.setUser(User.builder().userId(request.getUserId()).build());
        this.Payment.setProduct(Product.builder().id(request.getProductId()).build());

        return this.kakaoPayReadyResponse;
    }

    @Override
    public KakaoPayApproveResponse kakaoPayApprove(String pgToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

        Map<String, Object> payload = new HashMap<>();
        payload.put("cid", "TC0ONETIME");
        payload.put("tid", kakaoPayReadyResponse.getTid());
        payload.put("partner_order_id", Payment.getProduct().getId());
        payload.put("partner_user_id", Payment.getUser().getUserId());
        payload.put("pg_token", pgToken);

        HttpEntity<Map<String,Object>> body = new HttpEntity<>(payload, headers);

        ResponseEntity<KakaoPayApproveResponse> res = rt.postForEntity(
            HOST + "/online/v1/payment/approve", body, KakaoPayApproveResponse.class);

        return res.getBody();
    }
}