package org.kosa.tripTalk.payment;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;
import org.kosa.tripTalk.payment.KakaoPayRefundResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class KakaoPayRefundMockTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private KakaoPayServiceImpl kakaoPayService;

    @Test
    public void kakaoPayRefund_모의테스트() {
        // GIVEN
        String tid = "dummy-tid";
        Integer cancelAmount = 1000;

        KakaoPayRefundResponse.ApprovedCancelAmount cancelInfo =
                KakaoPayRefundResponse.ApprovedCancelAmount.builder()
                        .total(cancelAmount)
                        .build();

        KakaoPayRefundResponse mockResponse = KakaoPayRefundResponse.builder()
                .aid("A123456789")
                .tid(tid)
                .approvedCancelAmount(cancelInfo)
                .build();

        // WHEN
        ResponseEntity<KakaoPayRefundResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("https://kapi.kakao.com/v1/payment/cancel"),
                any(HttpEntity.class),
                eq(KakaoPayRefundResponse.class))
        ).thenReturn(responseEntity);

        // THEN
        ResponseEntity<KakaoPayRefundResponse> result = restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/payment/cancel",
                new HttpEntity<>(new LinkedMultiValueMap<>()), // 무의미한 더미 파라미터
                KakaoPayRefundResponse.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getAid()).isEqualTo("A123456789");
        assertThat(result.getBody().getApprovedCancelAmount().getTotal()).isEqualTo(1000);
    }
}