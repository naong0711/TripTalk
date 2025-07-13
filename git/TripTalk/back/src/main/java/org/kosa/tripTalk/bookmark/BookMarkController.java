package org.kosa.tripTalk.bookmark;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Slf4j
public class BookMarkController {
	private final BookMarkService bookMarkService;
	
	//검색어로 찾기위함
	@GetMapping("/search")
	public ResponseEntity<?> search(@RequestParam("keyword") String keyword){
		String result = bookMarkService.searchPlaces(keyword);
		
		return ResponseEntity.ok(result);
	}
	
	//위치 북마크하기 위한 메소드 
	@PostMapping
	public ResponseEntity<?> saveBookMark(@RequestBody BookMarkDTO bookMarkDTO){
		bookMarkService.saveBookMark(bookMarkDTO);
		return ResponseEntity.ok("북마크 성공");
	}
	
	//유저의 북마크한 곳 저장하기 위함 
	@GetMapping("/{userid}")
	public ResponseEntity<List<BookMark>> getUserBookMarks(@PathVariable Long userId){
		return ResponseEntity.ok(bookMarkService.getBookMarks(userId));
	}
	
	
}


