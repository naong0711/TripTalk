package org.kosa.tripTalk.jwt;

import java.util.Optional;
import org.kosa.tripTalk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  Optional<RefreshToken> findByToken(String token);
  Optional<RefreshToken> findByuserId(String userId);
  
}