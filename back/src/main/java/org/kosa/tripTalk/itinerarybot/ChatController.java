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
		//대화가 ㅇ벗을 경우 요청 자체를 거부하는 예외로 둠 
		if (input.getChatRequest() == null) {
	        throw new IllegalArgumentException("chatRequest는 null일 수 없습니다.");
	    }
		
		return chatService.chat(input.getChatRequest(), input.getUserInput());
	}
	
	@Data
	public static class ChatInput{
		//대화 전체 히스토리
		private ChatRequest chatRequest;
		//사용자의 입력 
		private String userInput;
	}

}
