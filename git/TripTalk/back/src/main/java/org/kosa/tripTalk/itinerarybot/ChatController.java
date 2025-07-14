package org.kosa.tripTalk.itinerarybot;

import java.util.List;
import org.kosa.tripTalk.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chatBot")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
	private final ChatService chatService;
	private final ChatRepository chatRepository;
	
	//대화요청
	@PostMapping
	public ResponseEntity<?> chat(@RequestBody ChatInput input, Authentication authentication) {
      if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
	  
	  //대화가 없을 경우 요청 자체를 거부하는 예외로 둠 
		if (input.getChatRequest() == null) {
	        throw new IllegalArgumentException("chatRequest는 null일 수 없습니다.");
	    }
		
		return ResponseEntity.ok(chatService.chat(input.getChatRequest(), input.getUserInput(), userId));
	}
	
	
	//user pk id로 대화 로그 조회
	@GetMapping("/my")
	public ResponseEntity<List<Chat>> getUserChatLogs(Authentication authentication) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    User user = (User) authentication.getPrincipal();
	    Long userId = user.getId();

	    List<Chat> chatLogs = chatRepository.findByUserIdOrderByCreatedAtAsc(userId);
	    return ResponseEntity.ok(chatLogs);
	}
}



