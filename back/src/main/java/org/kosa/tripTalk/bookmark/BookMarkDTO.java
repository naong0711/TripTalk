package org.kosa.tripTalk.bookmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookMarkDTO {
	private Long id;
	private String placeName;
	private String address;
	private double latitude;
	private double longitude;
	//private String category;
	private Long userId;
	private Long boardId;
	private String tempKey;

	public static BookMarkDTO fromEntity(BookMark entity) {
	 return BookMarkDTO.builder()
		        .id(entity.getId())
		        .placeName(entity.getPlaceName())
		        .address(entity.getAddress())
		        .latitude(entity.getLatitude())
		        .longitude(entity.getLongitude())
		        .boardId(entity.getBoard() != null ? entity.getBoard().getId() : null)
		        .userId(entity.getUser() != null ? entity.getUser().getId() : null)
		        .build();
	}

}
