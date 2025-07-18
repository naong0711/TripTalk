package org.kosa.tripTalk.favorite;

import java.util.List;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.reservation.Reservation;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
  //마이페이지 찜 추출용
  List<Favorite> findByUserUserId(String userId);

  boolean existsByUserAndProduct(User user, Product product);

  @Modifying
  @Transactional
  @Query("DELETE FROM Favorite f WHERE f.id = :favoriteId AND f.user.userId = :userId")
  void deleteByIdAndUserId(@Param("favoriteId") Long favoriteId, @Param("userId") String userId);

}
