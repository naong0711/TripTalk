package org.kosa.tripTalk.myPage;

import org.kosa.tripTalk.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/")
public class myPageController {
  
  private final MyService myService;
  
  //토큰에서 가져온 userid값으로 프로필 조회
  @GetMapping("profile")
  public ResponseEntity<?> profile(Authentication authentication) {
    
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    ProfileResponse response = myService.getProfile(userId);

    return ResponseEntity.ok(response);
  }
  
  //예약 목록 확인
  
  
  
  //찜 목록 확인
  
  //장바구니 확인
  
  

}
