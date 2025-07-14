package org.kosa.tripTalk.user;

import lombok.Getter;

@Getter
public class FindIdResponse {
  private String userId;

  public FindIdResponse(String userId) {
      this.userId = userId;
  }
}
