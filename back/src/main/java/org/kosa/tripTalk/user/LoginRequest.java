package org.kosa.tripTalk.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
  
  @NotBlank(message = "아이디는 필수입니다.")
  private String userId;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;

}
