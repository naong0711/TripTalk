package org.kosa.tripTalk.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatPayload {

  private String roomId;
  private String sender;
  private String message;
  
  
}
