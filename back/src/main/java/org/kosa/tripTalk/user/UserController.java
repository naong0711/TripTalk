package org.kosa.tripTalk.user;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/")
public class UserController {
  
  private final UserService userService;
  
  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
    
    System.out.println(request);    
    LoginResponse response = userService.login(request);
    
    return ResponseEntity.ok(response);
  }
  
  @GetMapping("checkId")
  public ResponseEntity<?> checkUserId(@RequestParam("userId") String userId) {
    boolean exists = userService.checkUserId(userId);
    return ResponseEntity.ok(exists);
  }
  
  
  @PostMapping("register")
  public ResponseEntity<?> register(@RequestBody @Valid UserRequest request) {

    System.out.println("============");
    System.out.println(request);
    UserResponse response = userService.register(request);
    
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("verify-password")
  public ResponseEntity<?> verifyPassword(
      @RequestBody Map<String, String> payload,
      @AuthenticationPrincipal User user
  ) {
    
    System.out.println("==========================");
    System.out.println(payload);
      boolean matched = userService.verifyPassword(user.getUserId(), payload.get("password"));
      if (!matched) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 불일치");
      }
      return ResponseEntity.ok().build();
  }
  
  
 

  

}
