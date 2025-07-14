package org.kosa.tripTalk.itinerarybot;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	@Value("${gemini.api-key}")
	private String apiKey;
	
	@Value("${gemini.api-url}")
	private String apiUrl; 
	
	//restTemplate : HTTP요청 보내기 도구(Spring)
	private final RestTemplate restTemplate = new RestTemplate();
	//ObjectMapper : JSON<-> Java 객체 변환 도구 
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ChatRepository chatRepository;
	
	
	public ChatRequest chat(ChatRequest request, String userInput, Long userId) {
	  
  	    //userId별 최초 인삿말 체크 후 DB에 저장
	    if (chatRepository.findById(userId).isEmpty()) {
  	      Chat greeting = Chat.builder()
  	          .role("model")
  	          .message("안녕하세요, 어디로 떠나고 싶으신가요? \n여러분의 일정을 짜드립니다! \n(어디를, 누구와, 얼마나 함께할까요?)")
  	          .createdAt(LocalDateTime.now())
  	          .userId(userId)
  	          .build();
  	      chatRepository.save(greeting);
  	    }
	  
	    // 사용자 입력 메시지 추가
	    request.addUserMessage(userInput);

	    // HTTP 요청 준비
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

	    // 외부 API 호출
	    String urlWithKey = apiUrl + "?key=" + apiKey;
	    ResponseEntity<ChatResponse> responseEntity = restTemplate.postForEntity(
	            urlWithKey, entity, ChatResponse.class);

	    ChatResponse response = responseEntity.getBody();
	    
	    if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
	        ChatResponse.Candidate candidate = response.getCandidates().get(0);
	        ChatResponse.Message modelMessage = candidate.getContent();

	        StringBuilder modelTextBuilder = new StringBuilder();
	        for (ChatResponse.Part part : modelMessage.getParts()) {
	            modelTextBuilder.append(part.getText());
	        }

	        String modelText = modelTextBuilder.toString();

	        // 모델 메시지 추가
	        request.addModelMessage(modelText);

	        // 사용자 메시지 저장
  	        Chat userChat = Chat.builder()
  	                .role("user")
  	                .message(userInput)
  	                .createdAt(LocalDateTime.now())
  	                .userId(userId)
  	                .build();
  	        chatRepository.save(userChat);
  
  	        // 모델 메시지 저장
  	        Chat modelChat = Chat.builder()
  	                .role("model")
  	                .message(modelText)
  	                .createdAt(LocalDateTime.now())
  	                .userId(userId)
  	                .build();
  	        chatRepository.save(modelChat);
	    }

	    return request;
	}
	}
	
	
	

