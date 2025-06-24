package org.kosa.tripTalk.OAuth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kosa.tripTalk.jwt.JwtUtil;

import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.User.Role;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Transactional
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
  
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {

      System.out.println("OAuth2LoginSuccessHandler 호출됨");
      OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
      
      // 1. 카카오 ID 추출
      String kakaoId = oAuth2User.getAttribute("id").toString(); // 소셜 고유 ID(socialId)
      String userId = "kakao_" + kakaoId;


      // 카카오 사용자 닉네임 추출
      Map<String, Object> properties = oAuth2User.getAttribute("properties");
      String nickname = (properties != null) ? properties.get("nickname").toString() : "카카오사용자";

      // DB에서 사용자 조회, 없으면 새로 생성
      User user = userRepository.findBySocialId(kakaoId)
          .orElseGet(() -> {
              return userRepository.save(User.builder()
                  .userId(userId)
                  .socialId(kakaoId)
                  .email(null) // 이메일 받아올 경우 설정
                  .name(nickname)
                  .nickname(userId) // 임시 nickname
                  .password("") // 소셜은 비밀번호 미사용
                  .role(User.Role.USER)
                  .phone("000-0000-0000") // 임시 처리, 나중에 수집
                  .loginType("KAKAO")
                  .emailVerified(true)
                  .build());
          });

      // JWT 토큰 생성
      String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getRole());
      String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

      //리프레시 토큰 쿠키 저장
      Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
      refreshTokenCookie.setPath("/");
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(false);
      refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 일주일
      response.addCookie(refreshTokenCookie);

      //엑세스 토큰 쿠키 저장
      Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
      accessTokenCookie.setPath("/");
      accessTokenCookie.setHttpOnly(true); // JS 접근 차단
      accessTokenCookie.setSecure(false);  // 배포 시 true (HTTPS)
      accessTokenCookie.setMaxAge(60 * 60); // 1시간
      response.addCookie(accessTokenCookie);

      response.sendRedirect("http://localhost:3000/oauth2");
  }
}
