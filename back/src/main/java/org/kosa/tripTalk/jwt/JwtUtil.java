package org.kosa.tripTalk.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.kosa.tripTalk.user.User;
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
//             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간
             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // 1분(테스트용)
             .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
             .compact();
  }
  
  //리프레시 토큰 생성(일주일)
  public String generateRefreshToken(String userId) {
    return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
//            .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7일
            .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60)) // 1시간(테스트용)
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
  }
  
  //토큰값으로 userId 추출
  public String extractUserId(String token) {
    System.out.println("🟡 전달된 토큰: " + token);
    try {
        var claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))  // ✅ 이 값도 null이면 안 됨
                .build()
                .parseClaimsJws(token)
                .getBody();

        String subject = claims.getSubject();
        System.out.println("🟢 추출된 subject(userId): " + subject);
        return subject;
    } catch (Exception e) {
        System.err.println("🔴 토큰 파싱 오류: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        return null; // or throw
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
            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    return expiration.before(new Date()); //현재 시간이 만료 시간 이후면 true
  }


}
