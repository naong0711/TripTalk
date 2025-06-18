package org.kosa.tripTalk.email;

import java.util.Optional;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

  Optional<Email> findByToken(String token);
  Optional<Email> findByUser(User user); //비밀번호 찾기용

}
