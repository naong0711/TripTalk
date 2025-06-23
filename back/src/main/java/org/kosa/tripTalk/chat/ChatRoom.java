package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import org.kosa.tripTalk.user.User;
import jakarta.persistence.Column;
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
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    private String id;  // 'user1_id'_'user2_id' 형식의 PK

    //사용자 1 (구매자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private User user1;

    //사용자 2 (판매자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private User user2;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist //서버시간 기준으로 저장 (!= DB 시간)
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.id == null && user1 != null && user2 != null) {
            Long userId1 = user1.getId();
            Long userId2 = user2.getId();
            if (userId1.compareTo(userId2) < 0) { //아이디값 비교하여 작은순정렬
                this.id = userId1 + "_" + userId2;
            } else {
                this.id = userId2 + "_" + userId1;
            }
        }
    }
}