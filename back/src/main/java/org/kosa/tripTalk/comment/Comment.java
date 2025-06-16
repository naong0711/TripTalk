package org.kosa.tripTalk.comment;

import java.time.LocalDateTime;

import org.kosa.tripTalk.travellog.TravelLog;
import org.kosa.tripTalk.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "tlog_id")
	private TravelLog tlog;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(unique = true, nullable = false)
	private String content;

	@Column(unique = true, nullable = false)
	private LocalDateTime createdAt;

}