<template>
  <div class="chat-container">
    <div class="chat-header">
    </div>
    
    <div class="chat-messages" ref="chatMessagesRef">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <div v-if="msg.role === 'model'" class="avatar">
          <img src="@/assets/chatBotBtn.png" alt="챗봇" />
        </div>

        <div class="bubble">{{ msg.content }}</div>
      </div>
    </div>

    <div class="chat-input">
      <input
        v-model="userInput"
        type="text"
        placeholder="메시지를 입력하세요..."
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage">전송</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'

// 로그인 후 발급받은 JWT 토큰
const token = localStorage.getItem('accessToken') || ''

const messages = ref([
  { role: 'model', content: '안녕하세요, 어디로 떠나고 싶으신가요? \n여러분의 일정을 짜드립니다! \n(어디를, 누구와, 얼마나 함께할까요?)' }
])

const userInput = ref('')
const chatMessagesRef = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatMessagesRef.value) {
    chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
  }
}

onMounted(async () => {
  if (!token) return

  try {
    const response = await axios.get(`/api/chatBot/my`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    messages.value = response.data.map(chat => ({
      role: chat.role === 'model' ? 'model' : 'user',
      content: chat.message
    }))
    await scrollToBottom()
  } catch (error) {
    console.error('대화 내역 불러오기 실패:', error)
  }
})

const sendMessage = async () => {
  const input = userInput.value.trim()
  if (!input) return

  messages.value.push({ role: 'user', content: input })
  userInput.value = ''
  await scrollToBottom()

  const requestBody = {
    userInput: input,
    chatRequest: {
      contents: messages.value.map(msg => ({
        role: msg.role === 'user' ? 'user' : 'model',
        parts: [{ text: msg.content }]
      }))
    }
  }

  try {
    const response = await axios.post('/api/chatBot', requestBody, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    const contents = response.data.contents || []
    const lastMessage = contents.length > 0 ? contents[contents.length - 1] : null
    const modelReplyText = lastMessage?.parts?.[0]?.text || '응답이 없어요. 다시 시도해주세요.'

    messages.value.push({ role: 'model', content: modelReplyText })
    await scrollToBottom()
  } catch (error) {
    console.error('챗봇 응답 실패:', error)
    messages.value.push({
      role: 'model',
      content: '서버 연결에 실패했어요. 다시 시도해주세요.'
    })
    await scrollToBottom()
  }
}
</script>

<style scoped>
.chat-container {
  max-width: 600px;
  margin: 0 0 40px auto;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  height: 90vh;
  font-family: 'Noto Sans KR', sans-serif;
  background-color: #fff;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
}

.chat-header {
  height: 80px;
  background-color: #292e4c;
  color: white;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  padding: 1.25rem;
  overflow-y: auto;
  background: #f7f7f7;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message {
  display: flex;
  align-items: flex-end;
}

.message.model {
  flex-direction: row;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 32px;
  height: 32px;
  margin-right: 8px;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
  background-color: #292e4c;
}

.bubble {
  position: relative;
  white-space: pre-line;
  max-width: 65%;
  padding: 0.75rem 1rem;
  word-break: break-word;
  font-size: 15px;
  line-height: 1.5;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-radius: 14px;
}

/* 챗봇 말풍선 + 꼬리 */
.message.model .bubble {
  background-color: #e0e0e0;
  color: #000;
  border-radius: 14px 14px 14px 0;
}

.message.user .bubble {
  position: relative;
  background-color: #292e4c;
  color: white;
  border-radius: 14px 14px 0 14px;
}

.chat-input {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #ccc;
  background: white;
  gap: 12px;
}

.chat-input input {
  flex: 1;
  padding: 12px;
  border: 1px solid #bbb;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.2s ease;
  outline: none;
  color: #222;
}

.chat-input input::placeholder {
  color: #bbb;
}

.chat-input input:focus {
  border-color: #292e4c;
}

.chat-input button {
  padding: 12px 24px;
  background-color: #292e4c;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.chat-input button:hover {
  background-color: #1d2138;
}

</style>

