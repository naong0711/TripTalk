import org.kosa.tripTalk.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@OneToOne
	@JoinColumn(name = "user_id", unique = true)
	@Id
	private User id;

	@Column(unique = true)
	private String businessNumber;

	@Column(nullable = false)
	private String businessName;

	@Column(nullable = false)
	private String contact;

}