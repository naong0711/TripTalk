package org.kosa.tripTalk.jwt;

import java.io.IOException;
import java.util.List;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

      String authHeader = request.getHeader("Authorization");

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
          String token = authHeader.substring(7);
          try {
              String userId = jwtUtil.extractUserId(token);

              if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                  User user = userService.loadUserByUserId(userId);

                  if (jwtUtil.validateToken(token, user)) {
                      UsernamePasswordAuthenticationToken authentication =
                              new UsernamePasswordAuthenticationToken(user, null,
                                      List.of(new SimpleGrantedAuthority(user.getRole().name())));

                      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                      SecurityContextHolder.getContext().setAuthentication(authentication);
                  }
              }

          } catch (JwtException | IllegalArgumentException e) {
              SecurityContextHolder.clearContext();
              // 필요 시 응답 상태 설정하거나 로그 남김
          }
          filterChain.doFilter(request, response);  // 한 번 호출
      } else {
          // 토큰 없을 때 그냥 필터 체인 진행
          filterChain.doFilter(request, response);  // 한 번 호출
      }
  }
}
