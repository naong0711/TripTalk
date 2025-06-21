package org.kosa.tripTalk.itinerarybot;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
	
	private final ChatService chatService;
	
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
	
	

}
