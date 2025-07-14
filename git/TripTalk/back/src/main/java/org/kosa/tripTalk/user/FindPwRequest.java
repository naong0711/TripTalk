package org.kosa.tripTalk.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPwRequest {
  @NotBlank
  private String userId;

  @Email
  @NotBlank
  private String email;

}