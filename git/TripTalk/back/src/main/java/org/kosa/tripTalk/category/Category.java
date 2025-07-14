package org.kosa.tripTalk.category;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(unique = true, nullable = false)
	private String kind;

	@Column(unique = true, nullable = false)
	private String description;

	@Column(unique = true, nullable = false)
	private String iconUrl;

}
