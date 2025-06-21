package org.kosa.tripTalk.myPage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private String userId;
    private String name;
    private String email;
    private String nickname;
    private String phone;
    private String loginType;
    
    
}