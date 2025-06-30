package org.kosa.tripTalk.email;

import java.time.LocalDateTime;
import java.util.UUID;

import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${app.email.verify-url-base}")
    private String verifyUrlBase;

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;
    private final UserRepository repository;

    //이메일 요청 내용
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("naong0711@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    //이메일 인증 요청
    public void sendVerificationEmail(User user, String token) {
        String verificationUrl = verifyUrlBase + token;
        String subject = "이메일 인증을 완료해주세요.";
        String text = "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n" + verificationUrl;
        System.out.println("sendVerificationEmail 호출됨 - 이메일: " + user.getEmail() + ", 토큰: " + token);
        sendEmail(user.getEmail(), subject, text);
    }


    //토큰 발급 요청
    public void createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        Email emailToken = Email.builder()
            .token(token)
            .user(user)
            .expiryDate(LocalDateTime.now().plusHours(24))
            .confirmed(false)
            .build();

        emailRepository.save(emailToken);
        sendVerificationEmail(user, token);
    }


    //토큰 이메일 인증
    public String verifyToken(String token) {
        Email emailToken = emailRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (emailToken.isConfirmed()) {
            return "이미 인증된 계정입니다.";
        }

        if (emailToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "토큰이 만료되었습니다.";
        }

        emailToken.setConfirmed(true);
        emailRepository.save(emailToken);

        User user = emailToken.getUser();
        user.setEmailVerified(true);
        repository.save(user);

        return "이메일 인증이 완료되었습니다.";
    }


    //인증 이메일 재발급 요청
    public void reissueVerificationEmailByUserId(String userId, String email) {
      
      User user = repository.findByUserId(userId)
          .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));

      if (Boolean.TRUE.equals(user.getEmailVerified())) {
          throw new IllegalStateException("이미 인증된 이메일입니다.");
      }
      
      // 이메일 임시 변경 (아직 DB 저장 X)
      user.setEmail(email);
      user.setEmailVerified(false); // 인증 필요 상태로 설정

      // 토큰 처리 및 발송
      Email emailToken = emailRepository.findByUser(user)
          .map(existing -> {
              existing.setToken(UUID.randomUUID().toString());
              existing.setExpiryDate(LocalDateTime.now().plusHours(24));
              existing.setConfirmed(false);
              return existing;
          })
          .orElseGet(() -> Email.builder()
              .token(UUID.randomUUID().toString())
              .user(user)
              .expiryDate(LocalDateTime.now().plusHours(24))
              .confirmed(false)
              .build()
          );

      emailRepository.save(emailToken);
      sendVerificationEmail(user, emailToken.getToken());
  }
}