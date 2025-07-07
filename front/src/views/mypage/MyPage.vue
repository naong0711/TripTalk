<template>
  <div class="mypage-container">
    <!-- 프로필 -->
    <div class="top-box">
      <div class="profile-box">
        <img :src="profileImage" class="profile-img" @error="profileImage = '/img/default-profile.jpg'" />
        <div class="profile-info">
          <h2 class="nickname" :title="nickname" @click="goToProfileDetail">{{ nickname }}</h2>
          <button @click="changeProfile" class="change-btn">사진 변경</button>
          <input type="file" ref="fileInput" @change="onImageSelected" accept="image/*" class="hidden" />
        </div>
      </div>
    
    <hr>

        <div class="quick-menu">
          <div class="menu-item" @click="goToCart">
            <span class="icon"><img src="@/assets/myPageBtn/cartBtn.png"></span>
            <span class="label">장바구니</span>
          </div>
          <div class="menu-item" @click="goToFavorites">
            <span class="icon"><img src="@/assets/myPageBtn/favoriteBtn.png"></span>
            <span class="label">찜 내역</span>
          </div>
          <!-- ✨ 채팅 메뉴 항목 (ChatModal 포함) -->
          <div class="menu-item" @click="openChat">
            <span class="icon"><img src="@/assets/myPageBtn/chatBtn.png"></span>
            <span class="label">채팅</span>
          </div>
        </div>

          <!-- 메뉴 바깥에서 모달 출력 -->
          <ChatModal v-if="isChatOpen" @close="isChatOpen = false">
            <ChatList @selectRoom="goToChatRoom" />
          </ChatModal>
      </div>

    <hr>

    <!-- 예약 내역 섹션 -->
    <div class="section">
      <div class="section-header">
        <h3>예약 내역</h3>
        <div class="nav-buttons">
          <button @click="prev('reservations')" :disabled="reservationPage === 0">‹</button>
          <button @click="next('reservations')" :disabled="reservationPage >= maxPage('reservations')">›</button>
        </div>
      </div>

      <div class="reservation-category">
        <div class="category-buttons">
          <button :class="{ active: activeCategory === '여행' }" @click="activeCategory = '여행'">여행</button>
          <button :class="{ active: activeCategory === '교통' }" @click="activeCategory = '교통'">교통</button>
          <button :class="{ active: activeCategory === '레저' }" @click="activeCategory = '레저'">레저</button>
        </div>
      </div>

      <div class="card-list">
        <div
          v-for="(item, index) in paginatedReservations"
          :key="item.id || 'more-reservations' + index"
          class="card"
          :class="{ 'more-card': item.isMore }"
          @click="item.isMore ? goToDetail('reservations') : goToItem(item, 'reservations')"
        >
          <template v-if="!item.isMore">
            <div class="reservation-id">예약번호: {{ item.id }}</div>
            <div class="card-title-wrapper">
              <p class="card-title" :title="item.title">{{ item.title }}</p>
            </div>
            <div class="card-footer">
              <svg class="icon-calendar" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
              <p class="card-date">{{ item.date }}</p>
            </div>
          </template>
          <template v-else>
            <div class="more-text">…</div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import ChatModal from '@/components/chat/ChatModal.vue'
import ChatList from '@/components/chat/ChatList.vue'

const router = useRouter()

const profileImage = ref('/img/default-profile.jpg')
const nickname = ref('닉네임 불러오는 중...')
const fileInput = ref(null)
const activeCategory = ref('여행')
const reservations = ref([])

const isChatOpen = ref(false)

function goToChatRoom(roomId) {
  router.push(`/chat/room/${roomId}`)
  isChatOpen.value = false
}

function openChat() {
  isChatOpen.value = true
}

// ✅ 마운트 시 프로필 정보 가져오기
onMounted(async () => {
  try {
    const res = await axios.get('/api/mypage/profile', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    console.log(res.data)
    console.log(res.data.profileImageUrl)
    profileImage.value = res.data.profileImageUrl || '/img/default-profile.jpg'
    nickname.value = res.data.nickname || '닉네임 없음'
  } catch (err) {
    console.error('프로필 불러오기 실패:', err)
    nickname.value = '로그인 필요'
  }

   // ✅ 예약 목록 요청
  try {
    const res = await axios.get('/api/mypage/reservationList', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })

    // 서버에서 받은 예약 데이터를 Vue 형식에 맞게 변환
    reservations.value = res.data.map(item => ({
      id: item.id,
      title: item.title,  // ← 서버 응답 필드에 맞게 수정
      date: formatDate(item.reservationDate) // ← 예: '2025-07-01'
    }))
    console.log(reservations.value)
  } catch (err) {
    console.error('예약 목록 불러오기 실패:', err)
  }
})

function formatDate(timestamp) {
  const date = new Date(timestamp)
  return date.toISOString().slice(0, 10) // yyyy-MM-dd 형식으로 자르기
}

// 프로필 사진 변경 핸들러
function changeProfile() {
  fileInput.value?.click()
}

async function onImageSelected(e) {
  const file = e.target.files[0]
  if (!file) return

  // 미리보기
  const reader = new FileReader()
  reader.onload = (ev) => {
    profileImage.value = ev.target.result
  }
  reader.readAsDataURL(file)

  // 업로드
  try {
    const formData = new FormData()
    formData.append('file', file)

    const res = await axios.post('/api/user/profile/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })

    if(res.data.url) {
      profileImage.value = res.data.url
      window.location.reload(true)
    }

    alert('변경이 완료되었습니다')

  } catch (err) {
    console.error('프로필 업로드 실패:', err)
    alert('프로필 업로드에 실패했습니다.')
  }

  fileInput.value.value = null // 같은 파일 다시 선택할 수 있게 초기화
}

const visibleCount = 3
const reservationPage = ref(0)
const postPage = ref(0)

function injectMoreCard(items, page) {
  const start = page * visibleCount
  const end = start + visibleCount
  const maxDisplayItems = 6
  let displayItems = items.slice(0, maxDisplayItems)
  const hasMore = items.length > maxDisplayItems

  if (hasMore && displayItems.length === maxDisplayItems) {
    displayItems[maxDisplayItems - 1] = { isMore: true }
  } else if (hasMore && displayItems.length < maxDisplayItems) {
    displayItems.push({ isMore: true })
  }

  return displayItems.slice(start, end)
}

const paginatedReservations = computed(() =>
  injectMoreCard(reservations.value, reservationPage.value)
)

function maxPage(type) {
  return 1 // 향후 동적으로 변경 가능
}

function next(type) {
  if (type === 'reservations' && reservationPage.value < maxPage(type)) reservationPage.value++
}

function prev(type) {
  if (type === 'reservations' && reservationPage.value > 0) reservationPage.value--
}

function goToItem(item) {
    router.push(`/mypage/reservationDetail/${item.id}`)
}

function goToProfileDetail() {
  router.push('/mypage/profile') // 원하는 라우터 경로로 변경 가능
}

function goToCart() {
  router.push('/cart')
}

function goToFavorites() {
  router.push('/favorites')
}

</script>

<style scoped>
h2 {
  cursor: pointer;
}

hr {
  border: none;
  border-top: 1px solid #eee;
}
.mypage-container {
  max-width: 480px;
  margin: 40px auto;
  padding: 24px;
  font-family: 'Noto Sans KR', sans-serif;
  box-sizing: border-box;
}

.top-box {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #faf7f2;
}

.profile-box {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 16px;
  margin-bottom: 30px;
}
.profile-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #ccc;
}
.profile-info {
  flex: 1;
  max-width: 360px;
}

.nickname {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.change-btn {
  font-size: 14px;
  color: #c8ad7f;
  background: none;
  border: none;
  cursor: pointer;
  text-decoration: underline;
}

.hidden {
  display: none;
}

.section {
  margin-bottom: 32px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.section-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.nav-buttons button {
  background: none;
  border: none;
  font-size: 22px;
  margin: 0 4px;
  color: #c8ad7f;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}
.nav-buttons button:hover:not(:disabled) {
  background-color: #f5efe3;
}
.nav-buttons button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: box-shadow 0.2s ease;
}

.card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.more-card {
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
  color: #c8ad7f;
  cursor: pointer;
  box-shadow: none !important;
}

.reservation-id {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
  text-align: left;
  user-select: none;
}

.card-title-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
  flex-grow: 1;
  text-align: left;
}

.card-date {
  font-size: 14px;
  color: #555;
  margin-top: 6px;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}

.icon img {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.icon-calendar {
  width: 18px;
  height: 18px;
  stroke: #c8ad7f;
}

.category-buttons {
  display: flex;
  justify-content: flex-start;
  gap: 0;
  margin: 0;
  border: 1px solid #c8ad7f;
  border-bottom: none;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  overflow: hidden;
  width: fit-content;
  line-height: 1;
}

.category-buttons button {
  padding: 10px 30px;
  font-size: 14px;
  background-color: white;
  color: #c8ad7f;
  border: none;
  border-right: 1px solid #c8ad7f;
  cursor: pointer;
  transition: 0.2s ease all;
}

.category-buttons button:last-child {
  border-right: none;
}

.category-buttons button.active,
.category-buttons button:hover {
  background-color: #c8ad7f;
  color: white;
}

.reservation-category {
  display: flex;
  align-items: flex-end;
  border-bottom: 2px solid #c8ad7f;
  height: 30px;
  margin-bottom: 10px;
}

.more-text {
  font-size: 30px;
  font-weight: 700;
  color: #c8ad7f;
}

@media (max-width: 600px) {
  .card {
    flex: 0 0 calc((100% - 12px) / 2);
  }
}
.quick-menu {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
  text-align: center;
}

.menu-item {
  flex: 1;
  cursor: pointer;
  padding: 12px 0;
  transition: background-color 0.2s;
  border-radius: 8px;
}

.menu-item:hover {
  background-color: #f5efe3;
}

.menu-item .icon {
  display: block;
  font-size: 24px;
  margin-bottom: 6px;
  color: #c8ad7f;
}

.menu-item .label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
</style>
