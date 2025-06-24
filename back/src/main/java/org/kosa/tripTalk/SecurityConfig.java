package org.kosa.tripTalk;

import org.kosa.tripTalk.OAuth2.OAuth2LoginSuccessHandler;
import org.kosa.tripTalk.jwt.JwtAuthenticationFilter;
import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.user.UserRepository;
import org.kosa.tripTalk.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  
  //스프링 시큐리티 설정
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
      http    .csrf(csrf -> csrf.disable()) //csrt 보호 비활성화
              .httpBasic(AbstractHttpConfigurer::disable) //httpBasic 인증 비활성화
              .formLogin(AbstractHttpConfigurer::disable) //스프링 시큐리티 기본 로그인 폼 비활성화
              .authorizeHttpRequests((authorize) -> authorize //요청경로 접근제어
                      .requestMatchers(
                          "/api/user/register",
                          "/",
                          "/api/user/login/**",
                          "/ws/**",
                          "/ws/**/**",
                          "/oauth2/**",
                          "/email/verify"
                      ).permitAll()
                      .requestMatchers("/api/mypage/**").authenticated()  //로그인한 사용자만 접근 가능
                      .anyRequest().authenticated())
              .oauth2Login(oauth2 -> oauth2
                  .successHandler(oAuth2LoginSuccessHandler())
              )
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              // 폼 로그인은 현재 사용하지 않음         
//            .formLogin(formLogin -> formLogin
//                    .loginPage("/login")
//                    .defaultSuccessUrl("/home"))
              .logout((logout) -> logout //로그아웃 요청시
                      .logoutSuccessUrl("/")
                      .invalidateHttpSession(true))
              .sessionManagement(session -> session //세션 삭제
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

      return http.build();
  }
  
  //비밀번호 암호화
  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
  }
  
//jwt 토큰 설정
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(UserService userService) {
      return new JwtAuthenticationFilter(jwtUtil, userService);
  }
  
 //oAuth2 설정 -> 카카오 로그인
  @Bean
  public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
      return new OAuth2LoginSuccessHandler(jwtUtil, userRepository);
  }
  
}