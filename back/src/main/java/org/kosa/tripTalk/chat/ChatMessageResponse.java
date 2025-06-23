package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {
    private String message;
    private String roomId;
    private Long senderId;
    private LocalDateTime sentAt;

    public ChatMessageResponse(ChatMessage message) {
        this.message = message.getMessage();
        this.roomId = message.getRoom().getId();
        this.senderId = message.getSender().getId();
        this.sentAt = message.getSentAt();
    }
}
