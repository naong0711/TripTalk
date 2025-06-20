package org.kosa.tripTalk.user;

import java.util.Date;
import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.jwt.RefreshToken;
import org.kosa.tripTalk.jwt.RefreshTokenRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserRepository repository;
  private final RefreshTokenRepository tokenRepository;
  private final JwtUtil jwtUtil;
  //회원가입 시 패스워드 암호화 - 미구현
  private PasswordEncoder passwordEncoder;

  public LoginResponse login(@Valid LoginRequest request) {
    
    //인증 시도
    System.out.println(request.getUserId());
    System.out.println(request.getPassword());
    
    //유저 정보 가져오기
    User user =  repository.findByUserId(request.getUserId())
        .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    
    //입력된 비밀번호와 유저 비밀번호 비교 (암호화x)
    if (!user.getPassword().equals(request.getPassword())) {
      throw new BadCredentialsException("비밀번호 틀림");
    }
    
    //토큰 생성
    String accessToken  = jwtUtil.generateAccessToken(user.getUserId(), user.getRole());
    String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
    
    RefreshToken token = new RefreshToken();
    token .setUserId(user.getUserId());
    token .setToken(refreshToken);
    token .setExpiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7));
    tokenRepository.save(token);
    
    // LoginResponse 객체 생성 후 반환
    return new LoginResponse(accessToken, user.getName(), user.getRole());
  }

  //유저 아이디로 유저 정보 가져오기
  public User loadUserByUserId(String userId) {
    
    User user =  repository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    
    return user;
  }

}
