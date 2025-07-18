package org.kosa.tripTalk.favorite;

import java.util.List;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
  //마이페이지 찜 추출용
  List<Favorite> findByUserUserId(String userId);

  boolean existsByUserAndProduct(User user, Product product);

}
