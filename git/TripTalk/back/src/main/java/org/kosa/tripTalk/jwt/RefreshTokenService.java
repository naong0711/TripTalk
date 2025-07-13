package org.kosa.tripTalk.jwt;

import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken createRefreshToken(String userId, String token, Date expiryDate) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUserId(userId);
    refreshToken.setToken(token);
    refreshToken.setExpiryDate(expiryDate);
    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public void deleteByUserId(String userId) {
    refreshTokenRepository.deleteById(userId);
  }
}