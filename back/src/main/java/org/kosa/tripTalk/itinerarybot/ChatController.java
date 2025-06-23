package org.kosa.tripTalk.itinerarybot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
	private final ChatService chatService;
	
	@PostMapping
	public ChatRequest chat(@RequestBody ChatInput input) {
		if (input.getChatRequest() == null) {
	        throw new IllegalArgumentException("chatRequest는 null일 수 없습니다.");
	    }
		
		return chatService.chat(input.getChatRequest(), input.getUserInput());
	}
	
	@Data
	public static class ChatInput{
		private ChatRequest chatRequest;
		private String userInput;
	}
	
	
	/*
	private final ChatService chatService;
	
	@PostMapping
	public ResponseEntity<ChatResponse> chat(@RequestBody List<Message> history){
		ChatResponse response = chatService.getChatResponse(history);
		
		return ResponseEntity.ok(response);
	}
	*/
	
	/*
	 * 단일방향 대화
	@PostMapping
	public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatRequest request){
		if(Objects.isNull(request)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		log.info("========chat zone========");
		log.info("request ={}", request);
		
		ChatResponse response = chatService.getChatResponse(request);
		
		return ResponseEntity.ok(Map.of("status", "ok", "response", response));
	}
	
	*/

}
