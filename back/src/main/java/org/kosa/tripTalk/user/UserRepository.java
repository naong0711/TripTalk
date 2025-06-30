package org.kosa.tripTalk.user;

import java.util.Optional;
import org.kosa.tripTalk.email.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{ 
  //id로 유저 존재하는지 확인
  Optional<User> findByUserId(String userId);

  //소셜로그인 시 유저 찾기
  Optional<User> findBySocialId(String socialId);
  
  //존재여부검사
  boolean existsByUserId(String userId);
  boolean existsByEmail(String email);
  boolean existsByNickname(String nickname);

  //이메일
  Optional<User> findByEmail(String email);

}