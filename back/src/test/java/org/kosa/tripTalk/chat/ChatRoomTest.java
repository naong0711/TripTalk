package org.kosa.tripTalk.chat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.kosa.tripTalk.user.User;

public class ChatRoomTest {

    @Test
    public void testPrePersistGeneratesId() {
        ChatRoom room = new ChatRoom();
        
        User user1 = new User();
        user1.setId(10L);
        user1.setUserId("user10");

        User user2 = new User();
        user2.setId(5L);
        user2.setUserId("user5");

//        room.setUser1(user1);
//        room.setUser2(user2);

        // @PrePersist 메서드 직접 호출 (실제 JPA 호출 전에 동작하는 메서드)
        room.prePersist();

        // id는 낮은 userId가 앞에 와야 함 ("5_10")
        assertEquals("5_10", room.getId());
    }

    @Test
    public void testPrePersistKeepsExistingId() {
        ChatRoom room = new ChatRoom();
        room.setId("custom_id");

        User user1 = new User();
        user1.setId(10L);

        User user2 = new User();
        user2.setId(5L);

//        room.setUser1(user1);
//        room.setUser2(user2);

        room.prePersist();

        // 이미 id가 있으면 변경하지 않음
        assertEquals("custom_id", room.getId());
    }
}
