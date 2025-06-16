package org.kosa.tripTalk.favorite;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.kosa.tripTalk.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "favorite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String ownerType;

    @Column
    private Long ownerId;

    @Column
    private LocalDateTime clickDate;

}