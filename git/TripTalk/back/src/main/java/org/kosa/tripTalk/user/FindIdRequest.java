package org.kosa.tripTalk.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindIdRequest {

    @NotBlank
    private String query;  // 이메일 또는 휴대폰 번호


}
