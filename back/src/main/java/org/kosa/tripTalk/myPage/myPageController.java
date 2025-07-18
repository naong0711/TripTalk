package org.kosa.tripTalk.myPage;

import java.nio.file.AccessDeniedException;
import java.util.List;
import org.kosa.tripTalk.cart.CartResponse;
import org.kosa.tripTalk.favorite.FavoriteResponse;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.reservation.ReservationResponse;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
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
    return ResponseEntity.ok(myService.getMyPageProfile(userId));
}
  
  //마이페이지 정보수정
  @PutMapping("profile/update")
  public ResponseEntity<?> profileUpdate(Authentication authentication, @RequestBody UserRequest request) {
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    ProfileResponse response = myService.profileUpdate(userId, request);
    
    System.out.println(response);

    return ResponseEntity.ok(response);
  }
  
  //마이페이지 탈퇴
  @PutMapping("profile/delete")
  public ResponseEntity<?> profileDelete(Authentication authentication) {
    
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    myService.profileDelete(userId);

    return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
  }
  
  
  //예약 목록 확인
  @GetMapping("reservationList")
  public ResponseEntity<?> reservationList(Authentication authentication) {
    
    //헤더에서 userid 추출
    User user = (User) authentication.getPrincipal();
    String userId = user.getUserId();
    
    List<ReservationResponse> responseList = myService.reservationList(userId);

    return ResponseEntity.ok(responseList);
  }
  
  //예약 상세 정보 조회
  @GetMapping("/reservation/{reservationId}")
  public ResponseEntity<?> getReservationDetail(@PathVariable("reservationId") Long id, Authentication authentication) throws AccessDeniedException {
     User user = (User) authentication.getPrincipal();
     String userId = user.getUserId();
     
     System.out.println(userId+"==================");
     System.out.println(id+"==================");
  
     ReservationResponse detail = myService.getReservationDetail(id, userId);
     
     return ResponseEntity.ok(detail);
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
  
  //찜 추가
  @PostMapping("/favorite/{productId}")
  public ResponseEntity<?> addFavorite(Authentication authentication, @PathVariable("productId") Long productId) {
      try {
          User user = (User) authentication.getPrincipal();
          String userId = user.getUserId();

          Long favoriteId = myService.addFavorite(userId, productId);

          return ResponseEntity.ok(favoriteId);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("찜 추가 중 오류가 발생했습니다.");
      }
  }
  
  //찜 삭제
  @DeleteMapping("/favorite/{favoriteId}")
  public ResponseEntity<?> deleteFavorite(@PathVariable("favoriteId") Long favoriteId) {
      // 사용자 인증 정보 확인 및 삭제 로직 수행
    try {
      myService.deleteFavorite(favoriteId);
      return ResponseEntity.ok("삭제되었습니다.");
  } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("삭제 중 오류가 발생했습니다.");
  }
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
  
  //장바구니 추가
  @PostMapping("cart/{productId}")
  public ResponseEntity<?> addCart(Authentication authentication, @PathVariable("productId") String productId) {
      User user = (User) authentication.getPrincipal();
      String userId = user.getUserId();

      Long cartId = myService.addCart(userId, productId);

      return ResponseEntity.ok(cartId);
  }
  
  //장바구니 삭제
  @DeleteMapping("/cart/{cartId}")
  public ResponseEntity<?> removeCart(@PathVariable("cartId") Long cartId) {
    System.out.println("=================="+cartId);
      try {
          myService.deleteById(cartId);  // 실제 삭제 로직은 서비스에서 처리
          return ResponseEntity.ok("삭제되었습니다.");
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("삭제 중 오류가 발생했습니다.");
      }
  }
  
  

}
