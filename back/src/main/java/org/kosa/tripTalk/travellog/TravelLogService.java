package org.kosa.tripTalk.travellog;

import java.time.LocalDateTime;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelLogService {
		private final TravelLogRepository travelLogRepository;
		private final UserRepository userRepository;
		private final CategoryRepository categoryRepository;
		
		/*
		public TravelLog write(TravelLog travelLog) {
			return travelLogRepository.save(travelLog);
			 
		}
		 */

		@Transactional
		public TravelLog write(TravelLogDTO article) {
			System.out.println("userId = " + article.getUserId());

			
			User users = userRepository.findById(article.getUserId())
	                .orElseThrow(() -> new IllegalArgumentException("판매자 유저가 존재하지 않습니다."));
			
			Category category = categoryRepository.findById(article.getCategoryId())
	                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
			
			
			TravelLog tlEntity = TravelLog.builder()
					 .title(article.getTitle())
					 .user(users)
					 .content(article.getContent())
					 .category(category)
					 .createdAt(LocalDateTime.now())
					 .build();
			
			return travelLogRepository.save(tlEntity);
			
		}
		
		/*
		public Optional logList(TravelLogListDTO list) {
			
			
		}
		*/
		

}


