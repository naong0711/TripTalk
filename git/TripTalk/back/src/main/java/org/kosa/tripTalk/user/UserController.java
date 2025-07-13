package org.kosa.tripTalk.user;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
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

  @PostMapping("login/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request) {
    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 없음");
    }

    String refreshToken = header.substring(7);

    try {
      String newAccessToken = userService.refreshAccessToken(refreshToken);
      return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
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

  @PostMapping("profile/image")
  public ResponseEntity<?> uploadProfileImage(@RequestPart("file") MultipartFile file,
                                              Authentication authentication) {
    try {
      User user = (User) authentication.getPrincipal();
      Long userId = user.getId();  // user.getUserId()는 String일 수 있음
      Map<String, Object> response = userService.uploadProfileImage(file, userId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", e.getMessage()));
    }
  }

  // ✅ 현재 로그인한 사용자 정보 조회 (ID, 닉네임 등)
  @GetMapping("me")
  public ResponseEntity<?> getCurrentUserInfo(@AuthenticationPrincipal User user) {
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
    }

    return ResponseEntity.ok(Map.of(
      "id", user.getId(),
      "userId", user.getUserId(),
      "nickname", user.getNickname()
    ));
  }
  
  //아이디찾기
  @PostMapping("findId")
  public ResponseEntity<?> findId(@RequestBody @Valid FindIdRequest request) {
      // 이메일 또는 휴대폰 번호로 아이디 조회
      String userId = userService.findUserIdByEmail(request.getQuery());
      if (userId == null) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 정보로 가입된 아이디가 없습니다.");
      }
      return ResponseEntity.ok(new FindIdResponse(userId));
  }
  
  //비번찾기
  @PostMapping("findPw")
  public ResponseEntity<?> findPw(@RequestBody @Valid FindPwRequest request) {
      boolean success = userService.requestPasswordReset(request.getUserId(), request.getEmail());
      if (!success) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 재설정 요청에 실패했습니다.");
      }
      return ResponseEntity.ok("비밀번호 재설정 이메일을 발송했습니다.");
  }
  
  //새비밀번호설정
  @PostMapping("changePassword")
  public ResponseEntity<?> changePassword(@RequestBody ChangePwRequest request, Authentication authentication) {
      
      User user = (User) authentication.getPrincipal();
      Long userId = user.getId();
      
      System.out.println(userId+"=userId");
      
    try {
          userService.changePassword(userId, request.getNewPassword(), request.getConfirmPassword());
          return ResponseEntity.ok("비밀번호 변경 완료");
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
  }
  

}
