package org.kosa.tripTalk.bookmark;

import java.util.List;
import java.util.Map;

import org.kosa.tripTalk.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public ResponseEntity<?> saveBookMark(@RequestBody BookMarkDTO bookMarkDTO,
			Authentication authentication){
	    User user = (User) authentication.getPrincipal();
	    Long userId = user.getId();
		
		bookMarkService.saveBookMark(bookMarkDTO,userId);
		return ResponseEntity.ok("북마크 성공");
	}
	
	//유저의 북마크한 곳 저장하기 위함 
	@GetMapping("/{userId}")
	public ResponseEntity<List<BookMarkResponseDTO>> getUserBookMarks(@PathVariable("userId") Long userId) {
	    return ResponseEntity.ok(bookMarkService.getBookMarkDTOs(userId));
	}
	
	@DeleteMapping("/{bookmarkId}")
	public ResponseEntity<Void> deleteBookmark(@PathVariable("bookmarkId") Long bookmarkId, Authentication authentication) {
	    User user = (User) authentication.getPrincipal();
	    Long userId = user.getId();

	    bookMarkService.deleteBookmark(bookmarkId, userId); // 서비스 계층에서 권한 확인 + 삭제
	    return ResponseEntity.ok().build();
	}
	
	@GetMapping("/get/{boardId}")
	public ResponseEntity<List<BookMarkDTO>> getBookmarksByBoardId(@PathVariable("boardId") Long boardId) {
	    
		System.out.println(boardId+"++++++++++++++++");
		System.out.println("adas");
		List<BookMarkDTO> bookmarks = bookMarkService.findByBoardId(boardId);
	    return ResponseEntity.ok(bookmarks);
	}
	
	 // ✅ tempKey로 임시 북마크 조회
    @GetMapping("/temp/{tempKey}")
    public ResponseEntity<List<BookMarkDTO>> getBookmarksByTempKey(@PathVariable("tempKey") String tempKey) {
        return ResponseEntity.ok(bookMarkService.findByTempKey(tempKey));
    }
    
    // ✅ 여행기 등록 후 북마크 연결 (tempKey -> boardId)
    @PutMapping("/link")
    public ResponseEntity<?> linkBookmarksToBoard(@RequestBody BookmarkLinkRequest request) {
    	System.out.println("hello");
    	bookMarkService.linkBookmarksToBoard(request.getBoardId(), request.getTempKey());
        return ResponseEntity.ok().build();
    }
	
	

}


