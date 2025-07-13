package org.kosa.tripTalk.bookmark;

import lombok.Data;

@Data
public class BookMarkDTO {
	private String placeName;
	private String address;
	private double latitude;
	private double longitude;
	//private String category;
	private Long userId;
	
}
