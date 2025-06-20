package org.kosa.tripTalk.travellog;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TravelLogDTO {
	//입력받은 값에 대한 DTO
	private Long id;
	private Long userId;
	private String title;
	private String content;
	private Long categoryId;
	private LocalDateTime createdAt;
	
	

}
