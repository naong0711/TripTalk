<template>
  <div class="chat-list">
    <p class="chat-list-title">채팅 목록</p>
    <ul>
      <li
        v-for="room in chatRooms"
        :key="room.roomId"
        @click="$emit('selectRoom', { roomId: room.roomId, receiverId: room.opponentId, userRole: room.userRole, opponentName: room.opponentName })"
        :class="room.userRole === 'BUYER' ? 'buyer' : 'seller'"
      >
      <div class="room-content">
        <div>
          <div class="header-line">
              <!--🛒:USER, 🏷️:SELLER-->
              <span class="role-icon">{{ room.userRole === 'BUYER' ? '🛒' : '🏷️' }}</span>
              <strong class="nickname">{{ room.opponentName }} 님</strong>
            </div>
            <div class="last-message">
              <span>{{ room.lastMessage }} {{ formatTime(room.sentAt) }}</span>
            </div>
          </div>
          <span v-if="room.unreadCount > 0" class="unread-badge">{{ room.unreadCount }}</span>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const chatRooms = ref([])

function formatTime(sentAt) {
  if (!sentAt) return ''
  const date = new Date(sentAt)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

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

.chat-list-title {
  font-weight: 700;  
  font-size: 1.5rem; 
  padding: 7px 0 12px 0;
  margin: 0;
  user-select: none;
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

.room-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.buyer {
  background-color: #e0f7fa;
}

.seller {
  background-color: #fff3e0;
}

.header-line {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.role-icon {
  font-size: 1.2rem;
  position: relative;
  top: 10px;
}

.nickname {
  position: relative;
  top: 2px;
  left: 7px;
  font-weight: bold;
  white-space: nowrap;
}

.last-message {
  color: #666;
  font-size: 0.9rem;
  margin-top: 3px;
  margin-left: 40px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* 길면 ... 처리 */
  flex: 1;
}

.unread-badge {
  background-color: #e53935;
  color: white;
  font-size: 0.75rem;
  font-weight: bold;
  width: 20px;
  height: 20px;
  line-height: 20px;
  border-radius: 50%;
  text-align: center;
  user-select: none;
  display: inline-block;
  margin-left: 8px;
  flex-shrink: 0;
  margin-right: 8px;
}
</style>
