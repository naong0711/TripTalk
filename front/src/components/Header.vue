<template>
  <header class="header">
    <!-- 로고 / 배너 영역 -->
    <div class="logo-area">
      <img src="@/assets/logo.png" alt="TripTalk 로고" class="logo-img" @click="goHome" />
    </div>

    <!-- 햄버거 버튼 (마우스 호버용) -->
    <div class="burger-wrapper">
      <button class="burger">☰</button>

      <!-- 버튼 / 네비게이션 영역 -->
      <nav class="nav-buttons">
        <!-- 여행 섹션 -->
        <div class="nav-section nav-primary">
          <router-link to="/productList" class="nav-btn">여행 예약</router-link>
          <router-link to="/boardlist" class="nav-btn">여행 후기</router-link>
        </div>

        <!-- 🔴 사용자 섹션 -->
        <div class="nav-section nav-user">
          <template v-if="!isLoggedIn">
            <router-link to="/register/agree" class="nav-btn register">회원가입</router-link>
            <router-link to="/loginForm" class="nav-btn login">로그인</router-link>
          </template>
          <template v-else>
            <!-- 채팅 아이콘 -->
            <div class="chat-icon-wrapper">
              <button class="chat-btn" @click="isChatOpen = true"><img src="@/assets/mypageBtn/chatBtn.png"></button>
              <span v-if="hasUnreadMessages" class="red-dot"></span>
            </div>
            <ChatModal v-if="isChatOpen" @close="isChatOpen = false">
              <ChatList @selectRoom="goToChatRoom" />
            </ChatModal>
            <button class="nav-btn" @click="logout">로그아웃</button>
              <router-link
                v-if="memberRole === 'ADMIN'"
                to="/admin"
                class="nav-btn"
              >
                관리자페이지
              </router-link>

              <router-link
                v-else
                to="/MyPage"
                class="nav-btn"
              >
                마이페이지
              </router-link>
          </template>
        </div>
      </nav>
    </div>
    <hr />
  </header>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ChatModal from '@/components/chat/ChatModal.vue'
import ChatList from '@/components/chat/ChatList.vue'
import axios from '@/axios.js'

const searchQuery = ref('')

const route = useRoute()
const router = useRouter()

const isLoggedIn = ref(false)
const isChatOpen = ref(false)
const memberRole = ref('') 
const hasUnreadMessages = ref(false) // ✅ 읽지 않은 메시지 여부 상태
let isCheckingUnread = false
let unreadCheckInterval = null // ✅ interval 핸들 저장

function goToChatRoom(roomId) {
  router.push(`/chat/room/${roomId}`)
  isChatOpen.value = false
}

// ✅ 읽지 않은 메시지 확인
async function checkUnreadMessages() {
  if (isCheckingUnread) return // 이전 요청이 끝나기 전이면 실행 안 함
  isCheckingUnread = true

  const token = localStorage.getItem('accessToken')
  if (!token) {
    stopUnreadPolling()
    hasUnreadMessages.value = false
    isLoggedIn.value = false
    isCheckingUnread = false
    return
  }

  try {
    const res = await axios.get('/api/chat/rooms', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    hasUnreadMessages.value = res.data.some(room => room.unreadCount > 0)
    isCheckingUnread = false
  } catch (error) {
    console.error('읽지 않은 메시지 확인 실패', error)

    if (error.response?.status === 401 || error.response?.status === 403) {
      logout()
    } else {
      stopUnreadPolling()
      setTimeout(() => {
        if (isLoggedIn.value) startUnreadPolling()
      }, 5000)
    }
    isCheckingUnread = false
  }
}

function checkLoginStatus() {
  const token = localStorage.getItem('accessToken')
  isLoggedIn.value = !!token

  if (token) {
    memberRole.value = localStorage.getItem('userRole') || ''
    startUnreadPolling()
  } else {
    stopUnreadPolling()
    hasUnreadMessages.value = false
    memberRole.value = ''
  }
}

function startUnreadPolling() {
  if (unreadCheckInterval) return
  checkUnreadMessages()
  unreadCheckInterval = setInterval(checkUnreadMessages, 2000)
}

function stopUnreadPolling() {
  if (unreadCheckInterval) {
    clearInterval(unreadCheckInterval)
    unreadCheckInterval = null
  }
}

onMounted(() => {
  checkLoginStatus()
})

watch(() => route.fullPath, () => {
  checkLoginStatus()
})

onUnmounted(() => {
  stopUnreadPolling()
})

function goHome() {
  router.push('/')
}

function logout() {
  localStorage.removeItem("accessToken")
  localStorage.removeItem("refreshToken")
  isLoggedIn.value = false
  router.push('/')
}
</script>

<style scoped>
.header {
  width: 100%;
  height: 80px;
  background-color: #f6f2ec;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 5vw;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-bottom: 2px solid black; /* 검은 하단 선 추가 */
  position: relative;
}

.logo-area {
  display: flex;
  align-items: center;
  flex: 1 1 300px;
}

.logo-img {
  height: 70px;
  object-fit: contain;
  cursor: pointer;
}

.burger-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.burger {
  font-size: 28px;
  background: none;
  border: none;
  cursor: pointer;
  color: #2c3e50;
  display: none;
}

.nav-buttons {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-primary {
  border-right: 1px solid #ccc;
  padding-right: 20px;
  margin-right: 20px;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-btn {
  color: #2c3e50;
  text-decoration: none;
  font-size: 16px;
  font-weight: bold;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  background-color: transparent;
  transition: all 0.2s ease;
  cursor: pointer;
}

.nav-btn:not(.chat-btn):hover {
  background-color: transparent;
  color: #2c3e50;
  text-decoration: underline;
  font-size: 17px;
}

.nav-user .nav-btn.register {
  font-weight: bold;
  font-size: 15px;
  padding: 6px 10px;
  background-color: transparent;
  border: none;
  color: #2c3e50;
  cursor: pointer;
}

/* 로그인 버튼 (버튼처럼 스타일링) */
.nav-user .nav-btn.login {
  font-size: 14px;
  padding: 6px 12px;
  border: 1px solid #2c3e50;
  border-radius: 16px;
  background-color: transparent;
  color: #2c3e50;
  transition: all 0.2s ease;
}

/* 로그인 호버 효과 */
.nav-user .nav-btn.login:hover {
  background-color: #2c3e50;
  color: white;
  text-decoration: none;
  transform: scale(1.05);
}

.chat-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  position: relative;
  padding: 6px;
}

.chat-btn:hover {
  background-color: transparent;
  color: inherit;
}
.chat-icon-wrapper {
  position: relative;
  display: inline-block;
}

.red-dot {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 8px;
  height: 8px;
  background-color: red;
  border-radius: 50%;
}

@media (max-width: 768px) {
  .burger {
    display: block;
  }

  .nav-buttons {
    display: none;
    flex-direction: column;
    background-color: #f6f2ec;
    position: absolute;
    top: 50px;
    right: 0;
    width: 160px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    z-index: 10;
  }

  .burger-wrapper:hover .nav-buttons {
    display: flex;
  }

  .nav-btn {
    width: 100%;
    text-align: left;
    padding: 12px 20px;
    border-radius: 0;
  }

  .header {
    flex-direction: row;
    align-items: center;
  }
}

.chat-btn img {
  width: 22px;
  height: 22px;
  object-fit: contain;
}
</style>
