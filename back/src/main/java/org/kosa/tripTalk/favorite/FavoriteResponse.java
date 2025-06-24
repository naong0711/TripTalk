package org.kosa.tripTalk.favorite;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponse {
  private Long id;
  private String title;
}
