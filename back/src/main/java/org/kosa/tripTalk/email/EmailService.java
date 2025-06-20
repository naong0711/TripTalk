package org.kosa.tripTalk.email;

import java.time.LocalDateTime;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    //이메일 인증 요청
    public void sendVerificationEmail(User user, String token) {
      String verificationUrl = verifyUrlBase + token;
      String subject = "이메일 인증을 완료해주세요.";
      String text = "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n" + verificationUrl;
      sendEmail(user.getEmail(), subject, text);
  }
    
    //요청 내용
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("naong0711@gmail.com"); // Gmail 주소
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
    
    //이메일 인증 확인
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
      
      //이메일 인증 완료 > 로그인 가능 처리
      User user = emailToken.getUser();
      user.setEmailVerified(true);
      repository.save(user);

      return "이메일 인증이 완료되었습니다.";
  }
}
