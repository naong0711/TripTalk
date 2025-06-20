package org.kosa.tripTalk.user;

import org.kosa.tripTalk.user.User.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
  
  private String token;
  private String name;
  private Role role;
 
}
