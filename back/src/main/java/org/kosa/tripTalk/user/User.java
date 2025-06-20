

package org.kosa.tripTalk.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String phone;

    @Column
    private String loginType;

    @Column(unique = true)
    private String socialId;
    
    @Column(name = "email_verified", nullable = true)
    private Boolean  emailVerified;
    
    
    
    public enum Role {
      USER, SELLER, ADMIN
  }

}

