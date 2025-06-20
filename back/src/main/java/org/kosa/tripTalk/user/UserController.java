package org.kosa.tripTalk.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
    
    LoginResponse response = userService.login(request);
    
    return ResponseEntity.ok(response);
  }
  
  
  @PostMapping("register")
  public ResponseEntity<?> register(@RequestBody @Valid UserRequest request) {
    
    UserResponse response = userService.register(request);
    
    return ResponseEntity.ok(response);
  }
  
  @DeleteMapping("logout")
  public ResponseEntity<?> logout() {
    //추후 js에서 토큰 삭제
//    function logout() {
//      localStorage.removeItem("accessToken");
//      localStorage.removeItem("refreshToken");
//      location.href = "/login";
//    }
    return ResponseEntity.ok("로그아웃 완료");
  }
  
  

}
