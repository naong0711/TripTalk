package org.kosa.tripTalk.seller;
import org.kosa.tripTalk.BooleanToYNConverter;
import org.kosa.tripTalk.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller {
	
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seller_seq_gen")
  private Long id;  // seller 고유 PK

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)  // seller.userid 컬럼이 user.id를 참조하는 FK
  private User user;

  @Column(nullable = false)
  private String bankName;

  @Column(unique = true, nullable = false)
  private String accountNumber;

  @Column(nullable = false)
  private String accountHolder;
  
  @Column(name = "IS_APPLY", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'F'")
  @Convert(converter = BooleanToYNConverter.class) //false -> 'F', true -> 'T' 자동 변환
  private Boolean isApply; //승인여부

}