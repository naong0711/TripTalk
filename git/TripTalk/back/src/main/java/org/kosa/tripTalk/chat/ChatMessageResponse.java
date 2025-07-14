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
    private Long senderId; //로그인한 유저
    private Long opponentId; //상대 유저
    private LocalDateTime sentAt;
    private Boolean isRead; //읽음여부
    private int unreadCount; //안읽은 메세지 개수

    public ChatMessageResponse(ChatMessage message, Long currentUserId) {
        this.message = message.getMessage();
        this.roomId = message.getRoom().getId();
        this.senderId = message.getSender().getId();
        this.sentAt = message.getSentAt();
        
        // 상대방 ID 계산
        Long user1 = message.getRoom().getCustomer().getId();
        Long user2 = message.getRoom().getSeller().getId();
        this.opponentId = (user1.equals(currentUserId)) ? user2 : user1;
        
        this.isRead = message.getIsReadForUser(currentUserId);
    }
}
