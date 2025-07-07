package org.kosa.tripTalk.seller;

import java.util.Optional;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUserId(Long userId);
	Optional<Seller> findByUser(User user);
}