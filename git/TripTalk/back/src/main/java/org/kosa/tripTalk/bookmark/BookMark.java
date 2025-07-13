package org.kosa.tripTalk.bookmark;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "bookmark")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMark {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String placeName;
	
	@Column
	private String address;
	
	//위도
	@Column
	private Double latitude;
	
	//경도
	@Column
	private Double longitude;
	
	/*
	@Column
	private String category;
	*/
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

}
