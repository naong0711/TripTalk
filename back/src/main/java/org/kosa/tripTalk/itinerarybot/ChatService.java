package org.kosa.tripTalk.itinerarybot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	
	public ChatResponse getChatResponse(ChatRequest request) {
		RestTemplate restTemplate = new RestTemplate();
		
		//헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		//요청 본문 설정
		HttpEntity<ChatRequest> httpEntity = new HttpEntity<>(request, headers);
		
		
		String urlWithKey = apiUrl + "?key=" + apiKey;
		 log.info("요청 URL = {}", urlWithKey);
		
		
		log.info("httpEntity = {}", httpEntity.toString());
		//JSON 출력용 로그
		ObjectMapper mapper = new ObjectMapper();
		try {
			log.info("보낼 JSON ={}", mapper.writeValueAsString(request));
		} catch(JsonProcessingException e) {
			e.printStackTrace();
		}
	
		//POST 요청 실행
		ResponseEntity<ChatResponse> response = restTemplate.postForEntity(urlWithKey, httpEntity, ChatResponse.class);
		
		return response.getBody();
		
	}
	
	

}
