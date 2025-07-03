<template>
  <div class="chat-list">
    <h3>채팅 목록</h3>
    <ul>
      <li
        v-for="room in chatRooms"
        :key="room.roomId"
        @click="$emit('selectRoom', { roomId: room.roomId, receiverId: room.opponentId, userRole: room.userRole, opponentName: room.opponentName })"
        :class="room.userRole === 'BUYER' ? 'buyer' : 'seller'"
      >
        <span class="role-icon">
          {{ room.userRole === 'BUYER' ? '🛒' : '🏷️' }}
        </span>
        <strong>{{ room.opponentName }} 님</strong><br />
        <small>
          최근 메시지: {{ room.lastMessageSenderRole === room.userRole ? '나' : room.opponentName }}: {{ room.lastMessage }}
        </small>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const chatRooms = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/chat/rooms', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    chatRooms.value = res.data 
    console.log(chatRooms.value)
  } catch (error) {
    console.error('Error loading chat rooms:', error)
  }
})
</script>

<style scoped>
.chat-list {
  overflow-y: auto;
  height: 100%;
}

.chat-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.chat-list li {
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.chat-list li:hover {
  background-color: #f9f9f9;
}

.buyer {
  background-color: #e0f7fa;
}

.seller {
  background-color: #fff3e0;
}

.role-icon {
  margin-right: 6px;
  font-size: 1.2rem;
  vertical-align: middle;
}
</style>
