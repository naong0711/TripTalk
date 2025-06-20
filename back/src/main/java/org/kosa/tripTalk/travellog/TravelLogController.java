package org.kosa.tripTalk.travellog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class TravelLogController {
	private final TravelLogService travelLogService;
	
	/*
	@GetMapping("/write")
	public String writeLog() {
		return "travellog";
	}
	*/
	
	//log 쓰기
	@PostMapping("/write")
	public ResponseEntity<?> write(@RequestBody TravelLogDTO article){
		travelLogService.write(article);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	/*
	//한 사람이 쓴 글 출력 (마이페이지에서 볼 내용 Or 키워드(id,제목 등)로 검색할 때)
	@GetMapping("/{id}")
	public Optional<TravelLogEntity> logList(@RequestHeader )
	List<?> logList(@RequestBody TravelLogListDTO list){
		travelLogService.logList(list);
		return "redirect:/";
	}
	*/
	
}

