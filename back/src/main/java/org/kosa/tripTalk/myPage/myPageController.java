package org.kosa.tripTalk.myPage;

import java.util.List;
import org.kosa.tripTalk.cart.CartResponse;
import org.kosa.tripTalk.favorite.FavoriteResponse;
import org.kosa.tripTalk.reservation.ReservationResponse;
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
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    ProfileResponse response = myService.getProfile(userId);

    return ResponseEntity.ok(response);
  }
  
  //유저 crud
  
  //예약 목록 확인
  @GetMapping("reservationList")
  public ResponseEntity<?> reservationList(Authentication authentication) {
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    List<ReservationResponse> responseList = myService.reservationList(userId);

    return ResponseEntity.ok(responseList);
  }
  
  //찜 목록 확인
  @GetMapping("favoriteList")
  public ResponseEntity<?> favoriteList(Authentication authentication) {
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    List<FavoriteResponse> responseList = myService.favoriteList(userId);

    return ResponseEntity.ok(responseList);
  }
  
  
  //장바구니 확인
  @GetMapping("cartList")
  public ResponseEntity<?> cartList(Authentication authentication) {
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    List<CartResponse> responseList = myService.cartList(userId);

    return ResponseEntity.ok(responseList);
  }
  
  

}
