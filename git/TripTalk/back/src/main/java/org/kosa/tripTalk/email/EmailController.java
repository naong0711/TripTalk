package org.kosa.tripTalk.email;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email/")
public class EmailController {
  
  private final EmailService emailService;
  
  //이메일 인증 여부 확인
  @GetMapping("validate")
  public ResponseEntity<Integer> validateEmailVerified(@RequestParam("userId") String userId) {
    System.out.println(userId);
    boolean verified = emailService.isEmailVerified(userId);
    return ResponseEntity.ok(verified ? 1 : 0);
  }
  
  //이메일 인증 토큰 발급
  @GetMapping("verify")
  public String verifyEmail(@RequestParam("token") String token) {
    return emailService.verifyToken(token);
  }
  
  //이메일 재발급요청
  @PostMapping("changeEmail")
  public ResponseEntity<?> changeEmail(@RequestBody Map<String, String> request) {
    String userId = request.get("userId");
    String email = request.get("email");
    
    System.out.println(userId);
    System.out.println(email);
    
    try {
      emailService.updateEmailAndReissueVerification(userId, email);
      return ResponseEntity.ok("이메일 변경 및 인증 메일 재발송 완료");
  } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
  }
}

  //이메일 재발급요청
  @PostMapping("reissue")
  public ResponseEntity<?> reissueEmail(@RequestBody Map<String, String> request) {
      try {
          String userId = request.get("userId");
          emailService.reissueVerificationEmail(userId);
          return ResponseEntity.ok("인증 메일 재발송 완료");
      } catch (IllegalStateException e) {
          // 이미 인증된 이메일일 때 예외 처리
          return ResponseEntity.badRequest().body(e.getMessage());
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
      }
  }
  
  

}
