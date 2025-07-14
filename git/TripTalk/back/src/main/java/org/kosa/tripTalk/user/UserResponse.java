package org.kosa.tripTalk.user;

import lombok.Getter;

@Getter
public class UserResponse {
  
  private String userId;
  private String name;
  private String email;
  private String nickname;
  private String phone;
  private String role;
  
  public UserResponse(String userId, String email, String name, String nickname, String phone, String role){
      this.userId = userId;
      this.name = name;
      this.email = email;
      this.nickname = nickname;
      this.phone = phone;
      this.role = role;
  }

  public static UserResponse fromEntity(User user) {
      return new UserResponse(
          user.getUserId(),
          user.getEmail(),
          user.getName(),
          user.getNickname(),
          user.getPhone(),
          user.getRole().name()
      );
  }

}
