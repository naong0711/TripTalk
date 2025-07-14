<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>여행 챗봇</h2>
    </div>
    
    <div class="chat-messages" ref="chatMessagesRef">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
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
  margin: 30px auto;
  border: 1px solid #ccc;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  height: 80vh;
  font-family: 'Segoe UI', sans-serif;
}

.chat-header {
  background-color: #3e8ed0;
  color: white;
  padding: 1rem;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  text-align: center;
}

.chat-messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  background: #f7f7f7;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message {
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.model {
  justify-content: flex-start;
}

.bubble {
  white-space: pre-line;
  max-width: 60%;
  padding: 0.75rem 1rem;
  border-radius: 15px;
  background-color: #e0e0e0;
  word-break: break-word;
}

.message.user .bubble {
  background-color: #3e8ed0;
  color: white;
}

.chat-input {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #ccc;
  background: white;
}

.chat-input input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 10px;
  margin-right: 0.5rem;
}

.chat-input button {
  padding: 0.75rem 1rem;
  background-color: #3e8ed0;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}
</style>
`
