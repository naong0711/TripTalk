package org.kosa.tripTalk.user;

import org.kosa.tripTalk.user.User.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
  
  private Long id;
  private String accessToken;
  private String refreshToken;
  private String name;
  private Role role;
  private Boolean tempPasswordUsed;
 
}
