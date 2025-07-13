package org.kosa.tripTalk.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatPayload {

  private String roomId;    // 방 아이디
  private Long senderId;    // 보낸 사람
  private Long receiverId;  // 받는 사람
  private String senderRole; // "USER" or "SELLER"
  private String message;   // 내용
  
}
