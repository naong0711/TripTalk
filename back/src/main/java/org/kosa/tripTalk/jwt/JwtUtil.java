package org.kosa.tripTalk.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.User.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
  
  @Value("${jwt.secret-key}")
  private String secretKey;
  
  //엑세스 토큰 생성(1시간)
  public String generateAccessToken(User user) {
    return Jwts.builder()
             .setSubject(user.getUserId()) // 사용자 로그인 ID (예: "user1")
             .claim("id", user.getId())    // DB PK (예: 2202)
             .claim("role", user.getRole().name())
             .setIssuedAt(new Date(System.currentTimeMillis()))
             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간
             .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
             .compact();
  }
  
  //리프레시 토큰 생성(일주일)
  public String generateRefreshToken(String userId) {
    return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7일
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
            .compact();
  }
  
  //토큰값으로 userId 추출
  public String extractUserId(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
  } catch (JwtException e) {
      // 로그 남기고 null 리턴 혹은 예외 재처리
      throw new IllegalArgumentException("Invalid JWT token", e);
  }
  }

   //토큰 유효성 검사
  public boolean validateToken(String token, User user) {
    
    final String userId = extractUserId(token);
    
    return (userId.equals(user.getUserId()) && !isTokenExpired(token));
  }

  //토큰 만료여부 검사
  private boolean isTokenExpired(String token) {
    Date expiration = Jwts.parserBuilder()
            .setSigningKey(secretKey.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    return expiration.before(new Date()); //현재 시간이 만료 시간 이후면 true
  }


}
