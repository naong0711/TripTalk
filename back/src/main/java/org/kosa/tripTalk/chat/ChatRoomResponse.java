package org.kosa.tripTalk.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
@AllArgsConstructor
public class ChatRoomResponse {
  private String roomId;
  private Long opponentId;
  private String opponentName;
  private String lastMessage;
  private String userRole; // "BUYER" or "SELLER"
  private String lastMessageSenderRole;
}
