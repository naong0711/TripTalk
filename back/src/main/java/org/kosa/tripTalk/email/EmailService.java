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

    // 이메일 요청 내용
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("naong0711@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // 인증 메일 발송
    private void sendVerificationEmail(User user, String token) {
        String verificationUrl = verifyUrlBase + token;
        String subject = "이메일 인증을 완료해주세요.";
        String text = "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n" + verificationUrl;
        System.out.println("sendVerificationEmail 호출됨 - 이메일: " + user.getEmail() + ", 토큰: " + token);
        sendEmail(user.getEmail(), subject, text);
    }

    // 토큰 생성 또는 업데이트 (재사용용)
    private Email createOrUpdateEmailToken(User user) {
        return emailRepository.findByUser(user)
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
    }

    // 토큰 생성, 저장, 이메일 발송 공통 처리 메서드
    private void generateAndSendToken(User user) {
        Email emailToken = createOrUpdateEmailToken(user);
        emailRepository.save(emailToken);
        sendVerificationEmail(user, emailToken.getToken());
    }

    // 회원가입 시 토큰 생성
    public void createVerificationToken(User user) {
        generateAndSendToken(user);
    }

    // 이메일 변경 및 인증 상태 초기화 + 인증 메일 재발급
    public void updateEmailAndReissueVerification(String userId, String newEmail) {
        User user = repository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));

        user.setEmail(newEmail);
        user.setEmailVerified(false);

        generateAndSendToken(user);
    }

    // 이메일 재발급만 (기존 이메일 유지, 인증 메일 재발송)
    public void reissueVerificationEmail(String userId) {
        User user = repository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }

        generateAndSendToken(user);
    }

    // 토큰 이메일 인증 처리
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
    
    //이메일 인증여부
    public boolean isEmailVerified(String userId) {
      return repository.findByUserId(userId)
          .map(user -> user.getEmailVerified() != null && user.getEmailVerified())
          .orElse(false);
  }
    
   
    //임시 비밀번호
    public void sendTemporaryPassword(String toEmail, String userId, String tempPassword) {
      String subject = "[TripTalk] 임시 비밀번호 안내";
      String body = String.format("""
          안녕하세요, TripTalk입니다.

          %s 님의 임시 비밀번호는 아래와 같습니다:

          임시 비밀번호: %s

          보안을 위해 로그인 후 반드시 비밀번호를 변경해 주세요.
          """, userId, tempPassword);

      sendEmail(toEmail, subject, body);
  }
    
    
}
