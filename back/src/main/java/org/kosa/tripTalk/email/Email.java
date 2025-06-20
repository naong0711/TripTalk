package org.kosa.tripTalk.email;

import java.time.LocalDateTime;

import org.kosa.tripTalk.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_tokens")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_tokens_seq_gen")
    @SequenceGenerator(name = "email_tokens_seq_gen", sequenceName = "email_tokens_seq", allocationSize = 1)
    private Long id;

    private String token; // 토큰(UUID)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 유저정보

    private LocalDateTime expiryDate; // 이메일 인증 토큰 만료일

    private boolean confirmed; // 이메일 인증 완료 여부
    
    
    
}