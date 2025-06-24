package org.kosa.tripTalk.myPage;

import java.util.List;
import org.kosa.tripTalk.reservation.ReservationResponse;
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
    
    private List<ReservationResponse> reservations;
}