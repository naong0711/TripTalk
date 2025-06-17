package org.kosa.tripTalk;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.travellog.TravelLog;
import org.kosa.tripTalk.travellog.TravelLogRepository;
import org.kosa.tripTalk.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class TravellogTests {
	@Autowired
	TravelLogRepository travelLogRepository;
	
	@Autowired
	CategoryRepository categoryRepository;

	@Test
	@Transactional
	@Commit
	void writeTest() {
		
		User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        TravelLog travelLog = TravelLog.builder()
            .title("Test TravelLog Entry")
            .content("테스트용 travel log 입니다.")
            .user(user)
            .category(category)
            .createdAt(LocalDateTime.now())
            .build();

        travelLogRepository.save(travelLog);

        log.info("TravelLog 저장 완료!");
		
	}
	
}
