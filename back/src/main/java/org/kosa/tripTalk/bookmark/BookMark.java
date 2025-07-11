package org.kosa.tripTalk.bookmark;

import java.time.LocalDateTime;

import org.kosa.tripTalk.travellog.TravelLog;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "bookmark")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class BookMark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String placeName;

    @Column
    private String address;

    // 위도
    @Column
    private Double latitude;

    // 경도
    @Column
    private Double longitude;

    // 여행 몇 일차인지 저장
//    @Column
//    private Integer day;

    /*
    @Column
    private String category;
    */

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    private TravelLog board;
    
    private LocalDateTime createdAt;
    
    @Column(name = "temp_key")
    private String tempKey;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
