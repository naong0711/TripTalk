package org.kosa.tripTalk.chat;

import java.time.LocalDateTime;
import org.kosa.tripTalk.seller.Seller;
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
    @JoinColumn(name = "customer_id")
    private User customer;

    //사용자 2 (판매자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private Seller seller;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist //서버시간 기준으로 저장 (!= DB 시간)
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
    
    
}