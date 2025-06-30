package org.kosa.tripTalk.email;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  
  //이메일 인증 토큰 발급
  @GetMapping("verify")
  public String verifyEmail(@RequestParam("token") String token) {
    return emailService.verifyToken(token);
  }
  
  
  //이메일 재발급요청
  @PostMapping("reissue")
  public ResponseEntity<?> reissueEmail(@RequestBody Map<String, String> request) {
    String userId = request.get("userId");
    String email = request.get("email"); // ← 프론트에서 같이 보냄
    
    try {
        emailService.reissueVerificationEmailByUserId(userId, email);
        return ResponseEntity.ok("인증 메일이 재발송되었습니다.");
    } catch (UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저가 없습니다.");
    } catch (IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
  

}
