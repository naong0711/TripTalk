package org.kosa.tripTalk.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank
    private String userId;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    @Pattern(
        regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
        message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678"
    )
    private String phone;
}