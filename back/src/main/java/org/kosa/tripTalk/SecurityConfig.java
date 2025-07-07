package org.kosa.tripTalk;

import org.kosa.tripTalk.OAuth2.CustomAuthorizationRequestResolver;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final ClientRegistrationRepository clientRegistrationRepository;
  
  //스프링 시큐리티 설정
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
 
    CustomAuthorizationRequestResolver customResolver = new CustomAuthorizationRequestResolver(
        clientRegistrationRepository, "/oauth2/authorization");
    
      http    
              .cors(cors -> {
                // CORS 설정을 빈으로 등록했으면 이걸 빈으로 참조 가능
                // 기본적으로 WebMvcConfigurer 등에서 CORS 설정 관리 권장
              })
              .csrf(csrf -> csrf.disable()) //csrt 보호 비활성화
              .httpBasic(AbstractHttpConfigurer::disable) //httpBasic 인증 비활성화
              .formLogin(AbstractHttpConfigurer::disable) //스프링 시큐리티 기본 로그인 폼 비활성화
              .authorizeHttpRequests((authorize) -> authorize //요청경로 접근제어
                      .requestMatchers(
                          "/api/user/register",
                          "/**",
                          "/api/user/login/**",
                          "/chat-ws/**",
                          "/chat-ws/**/**",
                          "/oauth2/**",
                          "/payments/**",
                          "/api/files/**",
                          "/api/sellers/**",
                          "/email/verify"
                      ).permitAll()
                      .requestMatchers("/api/mypage/**", "/api/chat/**").authenticated()  //로그인한 사용자만 접근 가능
                      .anyRequest().authenticated())
              .oauth2Login(oauth2 -> oauth2
                  .authorizationEndpoint(endpoint ->
                      endpoint.authorizationRequestResolver(customResolver)
                  )
                  .successHandler(oAuth2LoginSuccessHandler())
              )
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              .exceptionHandling(exception -> exception
                  .authenticationEntryPoint((request, response, authException) -> {
                      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                      response.setContentType("application/json;charset=UTF-8");
                      response.getWriter().write("{\"error\": \"Unauthorized\"}");
                  })
              )
              .logout(logout -> logout
                  .logoutSuccessUrl("/")
                  .invalidateHttpSession(true)
              )
              .sessionManagement(session -> session
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              );
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
  
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowCredentials(true);
      configuration.addAllowedOrigin("http://localhost:5173"); // 프론트 주소
      configuration.addAllowedHeader("*");
      configuration.addAllowedMethod("*");

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
}