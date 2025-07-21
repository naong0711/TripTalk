package org.kosa.tripTalk;

import java.util.List;

import org.kosa.tripTalk.OAuth2.CustomAuthorizationRequestResolver;
import org.kosa.tripTalk.OAuth2.OAuth2LoginSuccessHandler;
import org.kosa.tripTalk.jwt.JwtAuthenticationFilter;
import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.user.UserRepository;
import org.kosa.tripTalk.user.UserService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
import org.springframework.web.filter.CorsFilter;

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
      		  .cors(cors -> Customizer.withDefaults())
              .csrf(csrf -> csrf.disable()) //csrt 보호 비활성화
              .httpBasic(AbstractHttpConfigurer::disable) //httpBasic 인증 비활성화
              .formLogin(AbstractHttpConfigurer::disable) //스프링 시큐리티 기본 로그인 폼 비활성화
              .authorizeHttpRequests((authorize) -> authorize //요청경로 접근제어
                      .requestMatchers(
                          "/api/user/register",
                          "/**",
                          "/api/user/login",
                          "/api/product/search",
                          "/api/product",               // ← 이거 추가!
                          "/api/product/**",
                          "/chat-ws/**",
                          "/chat-ws/**/**",
                          "/oauth2/**",
                          "/payments/**",
                          "/api/files/**",
                          "/api/sellers/**",
                          "/email/verify",
                          "/api/map/**",
                          "/api/log/list"
                          
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
      configuration.setAllowedOrigins(List.of("http://192.168.182.128:8081")); 
      configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
      configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
      configuration.setMaxAge(3600L);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
  
}