package org.kosa.tripTalk.user;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import org.kosa.tripTalk.email.Email;
import org.kosa.tripTalk.email.EmailRepository;
import org.kosa.tripTalk.email.EmailService;
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
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final EmailRepository emailRepository;

  //로그인 요청
  public LoginResponse login(@Valid LoginRequest request) {
  
    //유저 정보 가져오기
    User user =  repository.findByUserId(request.getUserId())
        .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    
    //입력된 비밀번호와 유저 비밀번호 비교 (암호화x)
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("비밀번호 틀림");
  }
    
    //이메일 미인증 시
    if (!user.isEmailVerified()) {
      throw new IllegalStateException("이메일 인증 후 로그인할 수 있습니다. 이메일을 확인해 주세요.");
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

  //유저 아이디로 유저 정보 가져오기(jwt 요청용)
  public User loadUserByUserId(String userId) {
    
    User user =  repository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    
    return user;
  }
  
  //회원가입-아이디 중복 확인
  public void checkUserId(String userId) {
    if(repository.existsByUserId(userId)) {
      throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
    }
  }
  
  //회원가입-이메일 중복 확인
  public void checkEmail(String email) {
    if(repository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }
  }
  
  //회원가입-닉네임 중복 확인
  public void checkNickname(String nickname) {
    if(repository.existsByNickname(nickname)) {
      throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
    }
  }
  
  //회원가입
  public UserResponse register(UserRequest request) {
    
    //중복검사
    checkUserId(request.getUserId());
    checkEmail(request.getEmail());
    checkNickname(request.getNickname());
    
    
    //비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());
    
    User user = User.builder()
        .userId(request.getUserId())
        .name(request.getName())
        .email(request.getEmail())
        .nickname(request.getNickname())
        .phone(request.getPhone())
        .loginType("LOCAL")
        .password(encodedPassword) 
        .role(User.Role.USER)
        .emailVerified(false)
        .build();

    User savedUser = repository.save(user);
    
    //이메일 인증 토큰 생성
    String token = UUID.randomUUID().toString();
    Email emailToken = Email.builder()
        .token(token)
        .user(savedUser)
        .expiryDate(LocalDateTime.now().plusHours(24))
        .confirmed(false)
        .build();
    emailRepository.save(emailToken);

    //이메일 발송
    String subject = "이메일 인증을 완료해주세요.";
    String verificationUrl = "http://localhost:8080/email/verify?token=" + token;
    String text = "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n" + verificationUrl;
    emailService.sendEmail(savedUser.getEmail(), subject, text);

    return UserResponse.fromEntity(savedUser);
}

}
