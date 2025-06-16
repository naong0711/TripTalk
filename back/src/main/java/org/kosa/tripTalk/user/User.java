

package org.kosa.tripTalk.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    private String role;

    @Column
    private String phone;

    @Column
    private String loginType;

    @Column
    private String socialId;

}