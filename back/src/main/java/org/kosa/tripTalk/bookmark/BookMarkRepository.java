package org.kosa.tripTalk.bookmark;

import java.util.List;

import org.kosa.tripTalk.travellog.TravelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long>{
	List<BookMark> findByUserIdOrderByCreatedAtAsc(Long userId);
	
	@Query("select b from BookMark b where b.board.id = :boardId")
	List<BookMark> findByBoardIdOrderByCreatedAtAsc(@Param("boardId") Long boardId);
	
	List<BookMark> findByUserIdAndBoardOrderByCreatedAtAsc(Long userId, TravelLog board);
	List<BookMark> findByTempKey(String tempKey);
	
}
