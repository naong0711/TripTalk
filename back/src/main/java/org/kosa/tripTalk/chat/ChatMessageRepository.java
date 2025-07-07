package org.kosa.tripTalk.chat;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{

  //채팅방(room) 기준으로 sentAt 내림차순 정렬해서 가장 최신 메시지 1개 반환
  @Query(value = "SELECT * FROM chat_message WHERE room_id = :roomId ORDER BY sent_at DESC FETCH FIRST 1 ROW ONLY", nativeQuery = true)
  Optional<ChatMessage> findLatestMessageByRoomId(@Param("roomId") String roomId);

  //sentAt 내림차순 정렬해서 메세지 가져옴
  List<ChatMessage> findByRoom_IdOrderBySentAtAsc(String roomId);
  
  //읽지 않은 메세지만 조회해서 읽음처리
  @Modifying
  @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.room.id = :roomId AND m.sender.id <> :userId AND m.isRead = false")
  void markMessagesAsReadForUser(@Param("roomId") String roomId, @Param("userId") Long currentUserId);

  //안읽은 메세지 카운트
  @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.room.id = :roomId AND m.sender.id <> :userId AND m.isRead = false")
  int countUnreadMessages(@Param("roomId") String roomId, @Param("userId") Long userId);
}
