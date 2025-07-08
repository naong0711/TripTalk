<template>
  <header class="header">
    <!-- 로고 / 배너 영역 -->
    <div class="logo-area">
      <img src="@/assets/logo.png" alt="TripTalk 로고" class="logo-img" @click="goHome" />
    </div>


    <!-- ✅ 검색창 추가 -->
    <div class="search-box">
      <input type="text" v-model="searchQuery" placeholder="여행지를 검색해보세요" class="search-input" />
      <button class="search-button">검색</button>
    </div>


    <!-- 햄버거 버튼 (마우스 호버용) -->
    <div class="burger-wrapper">
      <button class="burger">☰</button>

      <!-- 버튼 / 네비게이션 영역 -->
      <nav class="nav-buttons">
          <router-link to="/boardlist" class="nav-btn">여행 게시판</router-link>
        <template v-if="!isLoggedIn">
          <router-link to="/register/agree" class="nav-btn">회원가입</router-link>
          <router-link to="/loginForm" class="nav-btn">로그인</router-link>
        </template>
        <template v-else>
        <!-- 채팅 아이콘 -->
          <div class="chat-icon-wrapper">
            <button class="chat-btn" @click="isChatOpen = true">💬</button>
            <span v-if="hasUnreadMessages" class="red-dot"></span>
          </div>
          <!-- 채팅 모달 -->
          <ChatModal v-if="isChatOpen" @close="isChatOpen = false">
            <ChatList @selectRoom="goToChatRoom" />
          </ChatModal>
          <button class="nav-btn" @click="logout">로그아웃</button>
          <router-link to="/MyPage" class="nav-btn">마이페이지</router-link>
        </template>
      </nav>
    </div>
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
const hasUnreadMessages = ref(false) // ✅ 읽지 않은 메시지 여부 상태
let unreadCheckInterval = null // ✅ interval 핸들 저장


function goToChatRoom(roomId) {
  router.push(`/chat/room/${roomId}`)
  isChatOpen.value = false
}

// ✅ 읽지 않은 메시지 확인
async function checkUnreadMessages() {
  const token = localStorage.getItem('accessToken')
  if (!token) {
    stopUnreadPolling()
    hasUnreadMessages.value = false
    return
  }

  try {
    const res = await axios.get('/api/chat/rooms', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    hasUnreadMessages.value = res.data.some(room => room.unreadCount > 0)
  } catch (error) {
    console.error('읽지 않은 메시지 확인 실패', error)

     // 401, 403 같은 인증 에러면 polling 멈추고 로그아웃 권장
  if (error.response?.status === 401 || error.response?.status === 403) {
    logout()
  } else {
    // 500 에러 등 서버 에러는 polling 멈추고 잠시 기다렸다 재시도 할 수도 있음
    stopUnreadPolling()
    // 필요하면 5초 후 polling 다시 시작하게 할 수도 있음
    setTimeout(() => {
      if (isLoggedIn.value) startUnreadPolling()
    }, 5000)
  }
  }
}


function checkLoginStatus() {
  isLoggedIn.value = !!localStorage.getItem('accessToken')

  if (isLoggedIn.value) {
    startUnreadPolling()
  } else {
    stopUnreadPolling()
    hasUnreadMessages.value = false
  }
}

function startUnreadPolling() {
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
  height: 100px;
  background-color: #f6f2ec;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 5vw;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-bottom: 2px solid black; /* ✅ 검은 하단 선 추가 */
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

.search-box {
  display: flex;
  align-items: center;
  gap: 7px;
  max-width: 600px;
  margin-left: 10px;
  margin-right: 250px; /* ✅ 오른쪽 마진 추가해서 왼쪽으로 이동 */
}

.search-input {
  width: 400px;          /* 원하는 가로 크기 고정 */
  height: 40px;          /* 원하는 높이 고정 */
  padding: 12px 23px;    /* placeholder와 텍스트 좌우 패딩 조절 */
  border: 2px solid #4a90e2;
  border-radius: 8px;
  outline: none;
  font-size: 15px;
  color: #333;
  box-sizing: border-box; /* 패딩 포함해서 크기 계산 */
}

.search-input::placeholder {
  color: #aaa;
}

.search-button {
  padding: 10px 25px;
  background-color: #4a90e2;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: #357abd;
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
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
  align-items: center;
  flex: 1 1 300px;
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
  transition: background 0.3s, color 0.3s;
}

.nav-btn:hover {
  background-color: #2c3e50;
  color: #ffffff;
}

.chat-btn {
  background-color: #f6f2ec;
  border: none;
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

.chat-icon-wrapper {
  position: relative;
  display: inline-block;
}

.red-dot {
  position: absolute;
  top: 0;
  right: -3px;
  width: 7px;
  height: 7px;
  background-color: red;
  border-radius: 50%;
}
</style>