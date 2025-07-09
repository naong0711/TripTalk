package org.kosa.tripTalk.user;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.kosa.tripTalk.email.EmailRepository;
import org.kosa.tripTalk.email.EmailService;
import org.kosa.tripTalk.file.File;
import org.kosa.tripTalk.file.FileService;
import org.kosa.tripTalk.jwt.JwtUtil;
import org.kosa.tripTalk.jwt.RefreshToken;
import org.kosa.tripTalk.jwt.RefreshTokenRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
  
  private final UserRepository repository;
  private final RefreshTokenRepository tokenRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final FileService fileService;

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
    if (user.getEmailVerified() == null || !user.getEmailVerified()) {
      throw new IllegalStateException("이메일 인증 후 로그인할 수 있습니다. 이메일을 확인해 주세요.");
  }
   
    //탈퇴 여부 확인(탈퇴 시 true)
    if (user.isDel()) {
      throw new IllegalStateException("탈퇴된 회원입니다.");
  }
    
    //토큰 생성
    String accessToken  = jwtUtil.generateAccessToken(user);
    String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
    
    System.out.println("tempPasswordUsed: " + user.getTempPasswordUsed());
    
    RefreshToken token = new RefreshToken();
    token .setUserId(user.getUserId());
    token .setToken(refreshToken);
//    token .setExpiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7));
    token .setExpiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60)); //테스트용 1시간
    tokenRepository.save(token);
    
    // LoginResponse 객체 생성 후 반환
    return new LoginResponse(user.getId(), accessToken, refreshToken, user.getName(), user.getRole(), user.getTempPasswordUsed());
  }
  
  //비밀번호 패턴 확인
  private void validatePasswordFormat(String password) {
    String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    if (!password.matches(pattern)) {
        throw new IllegalArgumentException("비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.");
    }
}

  //유저 아이디로 유저 정보 가져오기(jwt 요청용)
  public User loadUserByUserId(String userId) {
    
    User user =  repository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    
    return user;
  }
  
  //토큰 만료여부 및 재발급
  public String refreshAccessToken(String refreshToken) {
    
    String userId = jwtUtil.extractUserId(refreshToken);
    
//    System.out.println("++++++++++++++++++");
//    System.out.println("리프레시 토큰 요청"+userId);
//    System.out.println("++++++++++++++++++");

    RefreshToken savedToken = tokenRepository.findByuserId(userId)
        .orElseThrow(() -> new IllegalStateException("리프레시 토큰 없음"));
    
//    System.out.println("저장된 refreshToken DB: " + savedToken.getToken());
//    System.out.println("요청 받은 refreshToken: " + refreshToken);

    if (!savedToken.getToken().equals(refreshToken)) {
        throw new IllegalArgumentException("리프레시 토큰 불일치");
    }

    if (savedToken.getExpiryDate().before(new Date())) {
        throw new IllegalArgumentException("리프레시 토큰 만료");
    }

    User user = repository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));

    return jwtUtil.generateAccessToken(user);
}
  
  //회원가입-아이디 중복 확인
  public boolean checkUserId(String userId) {
      return repository.existsByUserId(userId);
  }
  
  //회원가입-이메일 중복 확인
  public boolean checkEmail(String email) {
    return repository.existsByEmail(email);
  }
  
  //회원가입-닉네임 중복 확인
  public boolean checkNickname(String nickname) {
    return repository.existsByNickname(nickname);
  }
  
  //회원가입
  public UserResponse register(UserRequest request) {
    
    //중복검사
    checkUserId(request.getUserId());
    checkEmail(request.getEmail());
    checkNickname(request.getNickname());
    
    //비밀번호 유효성
    validatePasswordFormat(request.getPassword());
    
    //전화번호 하이픈
    String formattedPhone = formatPhoneNumber(request.getPhone());
    request.setPhone(formattedPhone);
    
    
    //비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());
    
    User user = User.builder()
        .userId(request.getUserId())
        .name(request.getName())
        .email(request.getEmail())
        .nickname(request.getNickname())
        .phone(request.getPhone())
        .birthDate(request.getBirthDate())
        .zipcode(request.getZipcode())
        .address(request.getAddress())
        .addressDetail(request.getAddressDetail())
        .loginType("LOCAL")
        .password(encodedPassword) 
        .role(User.Role.USER)
        .emailVerified(false)
        .tempPasswordUsed(false) //임시비밀번호여부
        .build();

    User savedUser = repository.save(user);
    
    //이메일 인증 토큰 저장
    emailService.createVerificationToken(savedUser);

    return UserResponse.fromEntity(savedUser);
}
  
  //전화번호 값 하이픈 있는지 없는지 확인해서 반환
  private String formatPhoneNumber(String phone) {
      if (phone == null) {
          throw new IllegalArgumentException("전화번호가 없습니다.");
      }

      // 이미 하이픈이 포함되어 있으면 그대로 반환
      if (phone.contains("-")) {
          // 패턴에 맞는지 한번 더 체크할 수도 있음
          if (!phone.matches("^01[016789]-\\d{3,4}-\\d{4}$")) {
              throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678");
          }
          return phone;
      }

      // 하이픈 없으면 10자리 또는 11자리인지 체크 후 변환
      if (phone.length() == 11 && phone.matches("\\d{11}")) {
          return phone.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
      } else if (phone.length() == 10 && phone.matches("\\d{10}")) {
          return phone.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
      } else {
          throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다. 예: 01012345678 또는 010-1234-5678");
      }
  }
  
  //정보수정 시 비밀번호 확인
  public boolean verifyPassword(String userId, String inputPassword) {
    User user = repository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return passwordEncoder.matches(inputPassword, user.getPassword());
}

  public boolean isEmailVerified(String userId) {
    return false;
  }

  public User findByUserId(String userId) {
    return repository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
}
  
  
  //프로필 올리기
  public Map<String, Object> uploadProfileImage(MultipartFile file, Long userId) throws IOException {
    User user = repository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    // 기존 이미지 삭제
    if (user.getProfileImage() != null) {
      user.setProfileImage(null);
      repository.save(user);
    }

    File savedFile = fileService.saveFile(file, "USERS", userId, 1);
    user.updateProfileImage(savedFile);
    repository.save(user);

    return Map.of(
            "message", "프로필 이미지 변경 완료",
            "url", savedFile.getImageUrl()
    );
}

  public String findUserIdByEmail(String query) {
    // 이메일로 User 검색 후 아이디 리턴
    Optional<User> user = repository.findByEmail(query);

    return user.map(User::getUserId).orElse(null);
  }
  
  public boolean requestPasswordReset(String userId, String email) {
    Optional<User> userOpt = repository.findByUserIdAndEmail(userId, email);
    if (userOpt.isEmpty()) {
        return false;
    }

    User user = userOpt.get();

    // 임시 비밀번호 생성
    String tempPassword = generateTemporaryPassword();

    // 임시 비밀번호 암호화 후 저장
    user.setPassword(passwordEncoder.encode(tempPassword));
    user.setTempPasswordUsed(true);
    repository.save(user);

    // 임시 비밀번호 이메일 발송
    emailService.sendTemporaryPassword(user.getEmail(), user.getUserId(), tempPassword);
    return true;
}

  //임시비밀번호 생성
  private String generateTemporaryPassword() {
      int length = 10;
      String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*#?&";
      SecureRandom random = new SecureRandom();
      StringBuilder sb = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
          sb.append(chars.charAt(random.nextInt(chars.length())));
      }
      return sb.toString();
  }
  
  //비밀번호 변경
  public void changePassword(Long userId, String newPassword, String confirmPassword) {
    User user = repository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));
    if (!newPassword.equals(confirmPassword)) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
  }
  
    validatePasswordFormat(newPassword);
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setTempPasswordUsed(false);
    repository.save(user);
    
  }

}
