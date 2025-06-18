package org.kosa.tripTalk.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/")
public class UserController {
  
  private final UserService userService;
  
  @PostMapping("login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    
    LoginResponse response = userService.login(request);
    
    return ResponseEntity.ok(response);
  }

}
