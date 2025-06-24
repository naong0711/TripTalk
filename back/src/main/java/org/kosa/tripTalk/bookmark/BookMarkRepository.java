package org.kosa.tripTalk.bookmark;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long>{
	List<BookMark> findByUserId(Long userId);
}
