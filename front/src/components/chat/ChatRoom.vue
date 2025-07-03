<template>
  <div class="chat-room">
    <!-- ✅ 상단 상대 정보 영역 -->
    <div class="chat-header">
      <button @click="$emit('back')" class="back-btn">←</button>
      <div class="opponent-name">{{ opponentName }}</div>
    </div>

    <!-- ✅ 예약 상세 정보 (always shown) -->
    <!-- <div class="opponent-detail">
      <h4>예약 정보</h4>
      <ul v-if="reservations.length > 0">
        <li v-for="(r, index) in reservations" :key="index">
          {{ r.productName }} | {{ r.startDate }} ~ {{ r.endDate }} | 인원수: {{ r.guestCount }}명
        </li>
      </ul>
      <p v-else>예약 정보가 없습니다.</p>
    </div> -->

    <!-- ✅ 채팅 메시지 목록 -->
    <div class="chat-messages">
      <div
        v-for="msg in messages"
        :key="msg.id || msg.sentAt"
        :class="['chat-bubble', msg.senderId === userId ? 'my-msg' : 'other-msg']"
      >
        <span class="bubble-content">{{ msg.message }}</span>
        <span class="msg-time">{{ formatTime(msg.sentAt) }}</span>
      </div>
    </div>

    <!-- ✅ 입력창 -->
    <div class="chat-input">
      <input
        v-model="message"
        @keyup.enter="send"
        placeholder="메시지 입력..."
      />
      <button @click="send">전송</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import { Client } from '@stomp/stompjs'
import axios from 'axios'

const props = defineProps({
  roomId: String,
  receiverId: Number,
  userRole: String,
  opponentName: String,
})

const userId = Number(localStorage.getItem('userId'))
const message = ref('')
const messages = ref([])
const reservations = ref([])
const showInfoModal = ref(false)
let stompClient = null

stompClient = new Client({
  brokerURL: 'ws://localhost:8080/chat-ws',
  connectHeaders: {
    Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
  },
  reconnectDelay: 5000,
  debug: (msg) => console.log('[STOMP]', msg),
  onConnect: () => {
    stompClient.subscribe(`/sub/chat/room/${props.roomId}`, (message) => {
      const chatMessage = JSON.parse(message.body)
      messages.value.push(chatMessage)
      setTimeout(() => {
        const container = document.querySelector('.chat-messages')
        if (container) container.scrollTop = container.scrollHeight
      }, 100)
    })
  },
  onStompError: (frame) => console.error('STOMP 에러:', frame),
})

function connectWebSocket() {
  stompClient.activate()
}

function send() {
  if (!message.value.trim()) return
  const payload = {
    roomId: props.roomId,
    receiverId: props.receiverId,
    senderRole: props.userRole === 'SELLER' ? 'SELLER' : 'USER',
    message: message.value,
  }
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: '/app/chat/message',
      body: JSON.stringify(payload),
    })
    message.value = ''
  }
}

function formatTime(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}


onMounted(async () => {
  //채팅 기록 불러오기
  try {
    const res = await axios.get(`/api/chat/room/${props.roomId}/messages`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    messages.value = res.data

    // 예약 정보 조회
    // const resvRes = await axios.get(`/api/reservations/user/${props.receiverId}`)
    // reservations.value = resvRes.data

     reservations.value = [
      {
        productName: '제주 렌터카',
        startDate: '2025-07-12',
        endDate: '2025-07-14',
        guestCount: 2,
      }]

  } catch (error) {
    console.error('데이터 불러오기 실패:', error)
  }
  connectWebSocket()
})
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 580px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background-color: #fff;
}

/* 상단 상대방 정보 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  background-color: #f0f4f7;
  border-bottom: 1px solid #ddd;
}

.back-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #333;
  cursor: pointer;
  margin-right: 0.5rem;
}

.opponent-name {
  font-weight: bold;
  font-size: 1.1rem;
  color: #222;
}

/* 메시지 영역 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #fafafa;
}

.chat-bubble {
  display: flex;
  align-items: center;
  margin: 0.4rem 0;
  gap: 0.4rem;
}

.chat-bubble.my-msg {
  justify-content: flex-end;
}

.chat-bubble.other-msg {
  justify-content: flex-start;
}

.bubble-content {
  max-width: 60%;
  padding: 0.6rem 1rem;
  border-radius: 20px;
  background-color: #e0f7fa;
  color: #333;
  font-size: 1rem;
  word-break: break-word;
}

.my-msg .bubble-content {
  background-color: #d1ffd6;
}

.msg-time {
  font-size: 0.7rem;
  color: #aaa;
  user-select: none;
}

.my-msg .msg-time {
  order: -1;
}

.other-msg .msg-time {
  order: 1;
}

/* 입력창 */
.chat-input {
  display: flex;
  padding: 0.75rem;
  border-top: 1px solid #ddd;
  background-color: #fff;
}

.chat-input input {
  flex: 1;
  padding: 0.6rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.chat-input button {
  margin-left: 0.5rem;
  padding: 0.6rem 1rem;
  background-color: #1976d2;
  color: white;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
