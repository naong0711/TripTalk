package org.kosa.tripTalk.travellog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	//log 쓰기
	@PostMapping("/write")
	public ResponseEntity write(@RequestBody TravelLogDTO article){
		travelLogService.write(article);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	// 목록 중 하나 조회 (id로)
	@GetMapping("/list/{id}")
	public ResponseEntity<TravelLogListDTO> getLog(@PathVariable("id") Long id) {
	    TravelLogListDTO log = travelLogService.findById(id);
	    return ResponseEntity.ok(log);
	}

	// 전체 목록 조회
	@GetMapping("/list")
	public ResponseEntity<List<TravelLogListDTO>> getAllLogs() {
	    List<TravelLogListDTO> logs = travelLogService.findAll();
	    return ResponseEntity.ok(logs);
	}
	
	//글 수정
	@GetMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody TravelLogDTO updateDTO){
		travelLogService.update(id, updateDTO);
		return ResponseEntity.ok().build();
	}
	
	
	//글 삭제
	@DeleteMapping("/delete/{id}")
	public ResponseEntity deleteLog(@PathVariable("id") Long id) {
		travelLogService.deleteLog(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
}

