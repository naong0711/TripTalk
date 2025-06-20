package org.kosa.tripTalk.travellog;

import java.time.LocalDateTime;

import org.kosa.tripTalk.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TravelLogListDTO {
	//log 목록을 보기 위한 DTO
	private Long id;
	private Long userId;
	private String title;
	private String content;
	private LocalDateTime createdAt;
	

}
