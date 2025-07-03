package org.kosa.tripTalk.chat;

import java.util.List;
import org.kosa.tripTalk.seller.Seller;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

  List<ChatRoom> findByCustomerIdOrSellerId(Long customerId, Long sellerId);

  //역할조회-구매자
  List<ChatRoom> findByCustomer(User user);

  //역할조회-판매자
  List<ChatRoom> findBySeller(Seller seller);

}
