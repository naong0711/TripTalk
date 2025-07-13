package org.kosa.tripTalk.jwt;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RefreshToken {

  @Id
  private String userId;

  @Column(nullable = false, length = 1000)
  private String token;

  @Column(nullable = false)
  private Date expiryDate;

}
