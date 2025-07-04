package org.kosa.tripTalk.myPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.kosa.tripTalk.cart.Cart;
import org.kosa.tripTalk.cart.CartRepository;
import org.kosa.tripTalk.cart.CartResponse;
import org.kosa.tripTalk.favorite.Favorite;
import org.kosa.tripTalk.favorite.FavoriteRepository;
import org.kosa.tripTalk.favorite.FavoriteResponse;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.reservation.ReservationRepository;
import org.kosa.tripTalk.reservation.ReservationResponse;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.kosa.tripTalk.user.UserRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; 


@Service
@RequiredArgsConstructor
public class MyService {
  
  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;
  private final FavoriteRepository favoriteRepository;
  private final CartRepository cartRepository;
  
  
  public ProfileResponse getProfile(String userId) {
    
    //userId로 사용자 조회
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    return ProfileResponse.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .phone(user.getPhone())
        .loginType(user.getLoginType())
        .birthDate(user.getBirthDate() != null ? user.getBirthDate().format(DateTimeFormatter.ISO_DATE) : null)
        .zipcode(user.getZipcode())
        .address(user.getAddress())
        .addressDetail(user.getAddressDetail())
        .build();
}


  public List<ReservationResponse> reservationList(String userId) {

    List<Reservation> reservations = reservationRepository.findByUserUserId(userId);
    
    return reservations.stream()
    .map(reservation -> ReservationResponse.builder()
        .id(reservation.getId())
        .reservationDate(reservation.getReservationDate())
        .status(reservation.getStatus())
        .totalPrice(reservation.getTotalPrice())
        .title(reservation.getProduct().getTitle())
        .build())
    .collect(Collectors.toList());

  }



  public List<FavoriteResponse> favoriteList(String userId) {
    List<Favorite> favorites = favoriteRepository.findByUserUserId(userId);

    return favorites.stream()
        .filter(f -> f.getProduct() != null && f.getProduct().getCategory() != null)
        .map(f -> {
            Product p = f.getProduct();
            return FavoriteResponse.builder()
                .id(f.getId())
                .productId(p.getId())
                .title(p.getTitle())
                .price(p.getPrice())
                .categoryId(p.getCategory().getId())
                .build();
        })
        .collect(Collectors.toList());
}




  public List<CartResponse> cartList(String userId) {
    List<Cart> carts = cartRepository.findByUserUserId(userId);
    
    return carts.stream()
        .map(cart -> CartResponse.builder()
            .id(cart.getId())
            .title(cart.getProduct().getTitle())
            .price(cart.getProduct().getPrice())
            .categoryId(cart.getProduct().getCategory().getId())
            .startDate(cart.getProduct().getStartDate())
            .endDate(cart.getProduct().getEndDate())    
            .build())
        .collect(Collectors.toList());
  }


  public ProfileResponse profileUpdate(String userId, UserRequest request) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    user.setName(request.getName());
    user.setNickname(request.getNickname());
    user.setPhone(request.getPhone());
    user.setEmail(request.getEmail());
    user.setLoginType(request.getLoginType());
    user.setBirthDate(request.getBirthDate());
    user.setZipcode(request.getZipcode());
    user.setAddress(request.getAddress());
    user.setAddressDetail(request.getAddressDetail());

    userRepository.save(user);

    return ProfileResponse.builder()
        .userId(user.getUserId())
        .name(user.getName())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .phone(user.getPhone())
        .loginType(user.getLoginType())
        .birthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : null)
        .zipcode(user.getZipcode())
        .address(user.getAddress())
        .addressDetail(user.getAddressDetail())
        .reservations(null)  // 필요하면 예약 리스트 넣기
        .build();
}

  public void profileDelete(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    
    user.setIsDel(true);
    user.setDelAt(LocalDateTime.now());
    
    userRepository.save(user);
  }

  //장바구니 삭제
  public void deleteById(Long cartId) {
    if (!cartRepository.existsById(cartId)) {
        throw new IllegalArgumentException("해당 장바구니 항목이 존재하지 않습니다.");
    }
    cartRepository.deleteById(cartId);
}

  //찜 삭제
  public void deleteFavorite(Long favoriteId) {
    if (!favoriteRepository.existsById(favoriteId)) {
        throw new IllegalArgumentException("해당 찜 항목이 존재하지 않습니다.");
    }
    favoriteRepository.deleteById(favoriteId);
}
  
}