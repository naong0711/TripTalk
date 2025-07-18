package org.kosa.tripTalk.cart;

import java.util.List;
import org.kosa.tripTalk.favorite.Favorite;
import org.kosa.tripTalk.product.Product;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
  //마이페이지 장바구니용
  List<Cart> findByUserUserId(String userId);

  boolean existsByUserAndProduct(User user, Product product);
  
}
