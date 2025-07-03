package org.kosa.tripTalk.chat;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{

  //채팅방(room) 기준으로 sentAt 내림차순 정렬해서 가장 최신 메시지 1개 반환
  @Query(value = "SELECT * FROM chat_message WHERE room_id = :roomId ORDER BY sent_at DESC FETCH FIRST 1 ROW ONLY", nativeQuery = true)
  Optional<ChatMessage> findLatestMessageByRoomId(@Param("roomId") String roomId);

  List<ChatMessage> findByRoom_Id(String roomId);
}
