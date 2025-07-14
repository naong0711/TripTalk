package org.kosa.tripTalk.user;

import lombok.Data;

@Data
public class ChangePwRequest {
    private String newPassword;
    private String confirmPassword;
}