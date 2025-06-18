package org.kosa.tripTalk.travellog;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/log")
@RequiredArgsConstructor
public class TravelLogController {
	@Autowired
	TravelLogService travelLogService;
	
	@GetMapping("/write")
	public String writeLog() {
		return "travellog";
	}
	
	@PostMapping("/write")
	@Builder
	public String writeLog(TravelLog travelLog) {
	
		TravelLog article = TravelLog.builder()
									 .title(travelLog.getTitle())
									 //user는 가져와야 함
									 .user(travelLog.getUser())
									 .content(travelLog.getContent())
									 //카테고리 값 수정 필요
									 .category(travelLog.getCategory())
									 .createdAt(LocalDateTime.now())
									 .build();
		
		travelLogService.write(article);
		
		log.info(String.valueOf(article));
		
		return "redirect:/";
	}

	
}

