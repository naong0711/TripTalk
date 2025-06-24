package org.kosa.tripTalk.travellog;

import java.time.LocalDateTime;
import java.util.List;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelLogService {
		private final TravelLogRepository travelLogRepository;
		private final UserRepository userRepository;
		private final CategoryRepository categoryRepository;

		@Transactional
		public TravelLog write(TravelLogDTO article) {			
			User users = userRepository.findById(article.getUserId())
	                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
			
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
		
		@Transactional
		// 단일 조회
		public TravelLogListDTO findById(Long id) {
		    TravelLog log = travelLogRepository.findById(id)
		        .orElseThrow(() -> new IllegalArgumentException("해당 로그가 없습니다. id=" + id));

		    return TravelLogListDTO.builder()
		            .id(log.getId())
		            .userId((log.getUser()).getId())
		            .title(log.getTitle())
		            .content(log.getContent())
		            .createdAt(log.getCreatedAt()) 
		            .build();
		}

		// 전체 목록 조회
		public List<TravelLogListDTO> findAll() {
		    List<TravelLog> logs = travelLogRepository.findAll();

		    return logs.stream()
		            .map(log -> TravelLogListDTO.builder()
		                    .id(log.getId())
		                    .userId((log.getUser()).getId())
		                    .title(log.getTitle())
		                    .content(log.getContent())
		                    .createdAt(log.getCreatedAt())
		                    .build())
		            .toList();
		}

		//글 삭제 
		public void deleteLog(Long id) {
			TravelLog log = travelLogRepository.findById(id)
				.orElseThrow(()-> new IllegalArgumentException("해당 로그가 없습니다. 글 번호 =" + id));
			
			travelLogRepository.delete(log);	
		}

		@Transactional
		public  void update(Long id, TravelLogDTO updateDTO) {
			TravelLog existingLog = travelLogRepository.findById(id)
					.orElseThrow(()-> new IllegalArgumentException("해당 로그가 없습니다."));
			
			if(updateDTO.getTitle() != null) {
				existingLog.setTitle(updateDTO.getTitle());
			}
			
			if(updateDTO.getContent() != null) {
				existingLog.setContent(updateDTO.getContent());
			}
			
		}


}


