package org.kosa.tripTalk.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{ 
  //id로 유저 존재하는지 확인
  Optional<User> findByUserId(String userId);

}