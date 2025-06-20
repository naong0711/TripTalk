package org.kosa.tripTalk.email;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email/")
public class EmailController {
  
  private final EmailService emailService;
  
  @GetMapping("verify")
  public String verifyEmail(@RequestParam("token") String token) {
    return emailService.verifyToken(token);
  }
  

}
