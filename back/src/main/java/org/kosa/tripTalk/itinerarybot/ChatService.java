package org.kosa.tripTalk.itinerarybot;

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
	
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public ChatRequest chat(ChatRequest request, String userInput) {
		request.addUserMessage(userInput);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
		String urlWithKey = apiUrl + "?key=" + apiKey;
		
		ResponseEntity<ChatResponse> responseEntity = restTemplate.postForEntity(
				urlWithKey, entity, ChatResponse.class);
		
		ChatResponse response = responseEntity.getBody();
		if(response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
			ChatResponse.Candidate candidate = response.getCandidates().get(0);
            ChatResponse.Message modelMessage = candidate.getContent();

            StringBuilder modelTextBuilder = new StringBuilder();
            for (ChatResponse.Part part : modelMessage.getParts()) {
                modelTextBuilder.append(part.getText());
            }
            
            request.addModelMessage(modelTextBuilder.toString());
		}
		
		return request;
	
	}
		
	/*
	public ChatResponse getChatResponse(List<Message> history) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ChatRequest request = new ChatRequest(history);
		HttpEntity<ChatRequest> httpEntity = new HttpEntity<>(request);
		
		String urlWithKEy = apiUrl + "?key="+apiKey;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			log.info("보낼 JSON ={}", mapper.writeValueAsString(request));
		} catch(JsonProcessingException e) {
			e.printStackTrace();
		}
	
		//POST 요청 실행
		ResponseEntity<ChatResponse> response = restTemplate.postForEntity(urlWithKEy, httpEntity, ChatResponse.class);
		
		return response.getBody();	
	}
	*/
	
	/*
	 * 단일 채팅용
	public ChatResponse getChatResponse(ChatRequest request) {
		RestTemplate restTemplate = new RestTemplate();
		
		//헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, Object> requestBody = new HashMap<>();
		List<Map<String, Object>> contents = request.getContents();
		
		requestBody.put("contents", contents);
		
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
		
		
		//요청 본문 설정
		//HttpEntity<ChatRequest> httpEntity = new HttpEntity<>(request, headers);
		
		
		
		
		//Gemini에선 url값을 보내려면 필요함 
		//open API과 달리 RestTemplate요청에tjs ?key=API_KEY인 퀀리 파라미터 방식으로 인증함 
		String urlWithKey = apiUrl + "?key=" + apiKey;
		
		//log.info("요청 URL = {}", urlWithKey);
		//log.info("httpEntity = {}", httpEntity.toString());
		
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

	*/
	
	

}
