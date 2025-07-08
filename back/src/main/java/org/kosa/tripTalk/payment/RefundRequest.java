package org.kosa.tripTalk.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequest {
    private String tid;
    private Integer cancelAmount;
    
}