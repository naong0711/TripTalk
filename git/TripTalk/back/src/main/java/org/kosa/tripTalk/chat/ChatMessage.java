package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import org.kosa.tripTalk.BooleanToYNConverter;
import org.kosa.tripTalk.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //연결된 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    //보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false)
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "is_read", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'F'")
    @Convert(converter = BooleanToYNConverter.class) //false -> 'F', true -> 'T' 자동 변환
    private Boolean isRead; //읽음여부
    
    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
    }
    
    public Long getSenderId() {
        return sender != null ? sender.getId() : null;
    }

    public Boolean getIsReadForUser(Long currentUserId) {
      return this.isRead != null ? this.isRead : false;
    }

    public ChatMessageResponse toResponse(Long currentUserId) {
      return new ChatMessageResponse(this, currentUserId);
  }
}