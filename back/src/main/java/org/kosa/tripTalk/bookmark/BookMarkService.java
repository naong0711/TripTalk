package org.kosa.tripTalk.bookmark;

import java.util.List;

import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookMarkService {
	private final BookMarkRepository bookMarkRepository;
	private final UserRepository userRepository;

	@Value("${kakao.api.key}")
	private String apiKey;
	
	//키워드로 장소 검색
	public String searchPlaces(String keyword) {
		String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK "+apiKey);
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url,  HttpMethod.GET,entity, String.class);
		
		return response.getBody();
	}
	
	//북마크로 저장하기
	@Transactional
	public void saveBookMark(BookMarkDTO bookMarkDTO) {
		User user = userRepository.findById(bookMarkDTO.getUserId())
				.orElseThrow(()-> new IllegalArgumentException("사용자 없음"));	
		
		BookMark list = BookMark.builder()
								.placeName(bookMarkDTO.getPlaceName())
								.address(bookMarkDTO.getAddress())
								.latitude(bookMarkDTO.getLatitude())
						        .longitude(bookMarkDTO.getLongitude())
						      //.category(bookMarkDTO.getCategory())
						        .user(user)
						        .build();
		
		bookMarkRepository.save(list);
	}
	
	//북마크 목록 조회하기
	public List<BookMark> getBookMarks(Long userId){
		return bookMarkRepository.findByUserId(userId);
	}
	
	

}
