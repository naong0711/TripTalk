package org.kosa.tripTalk.user;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "생년월일은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "우편번호는 필수입니다.")
    private String zipcode;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String addressDetail;

    @NotBlank
    private String loginType;

}