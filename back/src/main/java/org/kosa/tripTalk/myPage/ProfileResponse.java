package org.kosa.tripTalk.myPage;

import java.util.List;
import org.kosa.tripTalk.reservation.ReservationResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {

    private String userId;
    private String name;
    private String email;
    private String nickname;
    private String phone;
    private String loginType;
    private String birthDate;
    private String zipcode;
    private String address;
    private String addressDetail;
    
    private List<ReservationResponse> reservations;
}