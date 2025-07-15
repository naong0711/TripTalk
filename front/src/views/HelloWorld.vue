<template>
  <div
    class="swiper-background"
    :class="{ glow: isGlowing }"
    :style="{ background: currentBackground, transition: 'background 0.8s ease' }"
  >
    <!-- 텍스트 오버레이 -->
    <div class="text-overlay">
      {{ currentText }}
      <div class="text-description">{{ currentDescription }}</div>
    </div>

    <!-- 화살표 버튼 -->
    <button class="nav-arrow left" @click="goPrev">❮</button>
    <button class="nav-arrow right" @click="goNext">❯</button>

    <!-- 슬라이드 이미지 -->
    <div class="swiper-inner">
      <Swiper
        ref="swiperRef"
        :modules="modules"
        :slides-per-view="1"
        :space-between="0"
        :loop="true"
        :pagination="{ clickable: true, el: '.custom-pagination' }"
        :autoplay="{ delay: 10000, disableOnInteraction: false }"
        class="mySwiper"
        @slideChange="onSlideChange"
        @swiper="onSwiperInit"
      >
        <SwiperSlide v-for="(img, index) in images" :key="index">
          <img :src="img" class="slide-image" />
        </SwiperSlide>
      </Swiper>
    </div>

    <!-- 커스텀 페이지네이션 -->
    <div class="custom-pagination"></div>

    <!-- 지역/날짜/인원수 선택 폼 -->
    <div class="package-form-wrapper">
      <form @submit.prevent="onSearch">
        <div class="form-row">
          <div class="form-group small-select">
            <label for="region">지역 선택</label>
            <select id="region" v-model="selectedRegion" required>
              <option disabled value="">지역을 선택하세요</option>
              <option v-for="(region, i) in regions" :key="i" :value="region">{{ region }}</option>
            </select>
          </div>

          <div class="form-group small-input">
            <label for="startDate">출발 날짜</label>
            <input type="date" id="startDate" v-model="startDate" required />
          </div>

          <div class="form-group small-input">
            <label for="endDate">종료 날짜</label>
            <input type="date" id="endDate" v-model="endDate" required />
          </div>

          <div class="form-group small-input">
            <label for="peopleCount">인원수</label>
            <input type="number" id="peopleCount" v-model="peopleCount" min="1" required />
          </div>

          <button type="submit" class="search-btn small-btn">조회</button>
        </div>
      </form>
    </div>
  </div>

<div class="recommended-section">
  <h2 @click="goToProductList" style="cursor: pointer; user-select: none;">
    추천 상품
  </h2>
  <div class="recommended-list">
    <div
      class="recommended-card"
      v-for="product in recommendedProducts.slice(0, 5)"
      :key="product.id"
      @click="goToDetail(product.id)"
>
    
      <img :src="product.image" alt="추천 상품 이미지" />
      <div class="recommended-info">
        <h4>{{ product.title }}</h4>
        <p>{{ product.address }}</p>
        <p class="price">{{ product.price.toLocaleString() }}원 / 박</p>
      </div>
    </div>
  </div>
</div>

<!-- 챗봇 모달 -->
<ChatBotModal v-if="showChat" :close="() => showChat = false" />

<!-- 챗봇 안내 문구 + 버튼 -->
<div class="fixed-chat-wrapper">
  <div class="chat-hint-text">어디로 떠날지 고민될 땐?</div>
  <button class="fixed-chat-button" @click="showChat = true">
    <img src="@/assets/chatBotBtn.png" />
  </button>
</div>
</template>

<script setup>
import axios from 'axios'
import 'swiper/css'
import 'swiper/css/pagination'
import 'swiper/css/autoplay'
import ChatBotModal from '@/components/chatBot/ChatBotModal.vue' 

import { Swiper, SwiperSlide } from 'swiper/vue'
import { Pagination, Autoplay } from 'swiper/modules'
import { ref , onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const modules = [Pagination, Autoplay]
const swiperRef = ref(null)
const swiperInstance = ref(null)
const showChat = ref(false)

const onSwiperInit = (swiper) => {
  swiperInstance.value = swiper
}

// ✅ 지역 목록 동적 할당용
const regions = ref([]) // backgroundTexts 대신 사용
const selectedRegion = ref('')
const startDate = ref('')
const endDate = ref('')
const peopleCount = ref(1)

// ✅ 지역 목록 불러오기
const fetchRegions = async () => {
  try {
    const res = await axios.get('/api/product/locations')
    regions.value = res.data
  } catch (e) {
    console.error('지역 목록 불러오기 실패:', e)
  }
}

const images = [
  new URL('@/assets/busan.jpg', import.meta.url).href,
  new URL('@/assets/daejeon.jpg', import.meta.url).href,
  new URL('@/assets/seoul.jpg', import.meta.url).href,
  new URL('@/assets/jeju.jpg', import.meta.url).href,
  new URL('@/assets/gyeongju.jpg', import.meta.url).href,
  new URL('@/assets/gangneung.jpg', import.meta.url).href,
  new URL('@/assets/jeonju.jpg', import.meta.url).href
]

const backgrounds = [
  'linear-gradient(135deg, #6dd5fa, #4a9ff4)',
  'linear-gradient(135deg, #FFDD95, #FBB464)',
  'linear-gradient(135deg, #ffc9cc, #FF9AA2)',
  'linear-gradient(135deg, #b8ffc3, #4ddd66)',
  'linear-gradient(135deg, #C3F2E3, #6ce1bc)',
  'linear-gradient(135deg, #e2e7ff, #A0A8F5)',
  'linear-gradient(135deg, #FFDAC1, #ff9d81)'
]

const backgroundTexts = ['Busan', 'Daejeon', 'Seoul', 'Jeju', 'Gyeongju', 'Gangneung', 'Jeonju']
const backgroundDescriptions = [
  '부산의 활기찬 바다와 맛있는 해산물을 경험하세요.',
  '대전의 과학과 자연이 어우러진 도시입니다.',
  '서울에서 현대와 전통의 조화를 느껴보세요.',
  '제주의 아름다운 자연과 평화로운 휴식처.',
  '경주의 역사와 문화가 숨쉬는 고도입니다.',
  '강릉의 푸른 바다와 커피 향기를 즐기세요.',
  '전주의 전통 한옥과 맛있는 음식의 도시.'
]

const currentBackground = ref(backgrounds[0])
const currentText = ref(backgroundTexts[0])
const currentDescription = ref(backgroundDescriptions[0])
const isGlowing = ref(false)

function onSlideChange(swiper) {
  const realIndex = swiper.realIndex
  currentBackground.value = backgrounds[realIndex]
  currentText.value = backgroundTexts[realIndex]
  currentDescription.value = backgroundDescriptions[realIndex]

  isGlowing.value = true
  setTimeout(() => {
    isGlowing.value = false
  }, 600)
}

const onSearch = () => {
  router.push({
    path: '/productList',
    query: {
      location: selectedRegion.value,
      checkIn: startDate.value,
      checkOut: endDate.value,
      people: peopleCount.value
    }
  })
}

const goPrev = () => {
  swiperInstance.value?.slidePrev()
}
const goNext = () => {
  swiperInstance.value?.slideNext()
}

const recommendedProducts = ref([])

function goToProductList() {
  router.push({
    path: '/productList',
    query: {
      location: selectedRegion.value,
      checkIn: startDate.value,
      checkOut: endDate.value,
      people: peopleCount.value
    }
  })
}

function goToDetail(productId) {
  router.push({
    path: `/productDetail/${productId}`,
    query: {
      checkIn: startDate.value,
      checkOut: endDate.value,
      adults: peopleCount.value
    }
  })
}

onMounted(async () => {
  await fetchRegions()

  try {
    const res = await axios.get('/api/product?size=5')
    recommendedProducts.value = res.data.content.map(p => ({
      id: p.id,
      title: p.title,
      address: p.address,
      price: p.price,
      image: `/api/files/image/product/${p.id}`
    }))
  } catch (e) {
    console.error('추천 상품 불러오기 실패:', e)
  }
})
</script>


<style scoped>
@keyframes glow {
  0% {
    filter: brightness(1);
  }
  50% {
    filter: brightness(1.1);
  }
  100% {
    filter: brightness(1);
  }
}

.swiper-background.glow {
  animation: glow 0.6s ease;
}

.swiper-background {
  width: 100%;
  min-height: 60vh;
  padding: 50px 20px;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: background 0.8s ease;
  position: relative;
  overflow: hidden;
}

.text-overlay {
  position: absolute;
  top: 100px;
  left: 8.5%;
  font-size: 6.5rem;
  font-weight: 900;
  color: rgba(0, 0, 0, 0.4);
  user-select: none;
  pointer-events: none;
  z-index: 0;
  line-height: 1.2;
}

.text-description {
  font-size: 1.8rem;
  font-weight: 400;
  color: rgba(0, 0, 0, 0.5);
  margin-top: 8px;
  pointer-events: none;
  user-select: none;
  word-break: keep-all; /* ✅ 단어 단위로 줄바꿈 */
  max-width: 400px;
}

.swiper-inner {
  width: 40%;
  height: 40%;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  overflow: visible;
  position: relative;
  margin-left: 960px;
  margin-top: auto;
  z-index: 1;
}

::v-deep .swiper-pagination-bullet {
  background-color: #a0a8b8  !important;
  opacity: 1;
}

::v-deep .swiper-pagination-bullet-active {
  background-color: #2c3e50 !important;
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: opacity 0.8s ease;
  border-radius: 0;
}

.nav-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  font-size: 2.5rem;
  background: transparent;
  border: none;
  cursor: pointer;
  color: white;
  font-weight: bold;
  z-index: 2;
  padding: 0 12px;
}
.nav-arrow.left {
  left: 2%;
}
.nav-arrow.right {
  right: 2%;
}

.custom-pagination {
  position: absolute;
  bottom: 10px;
  left: 0;
  right: 0;
  text-align: center;
  z-index: 1;
}

.package-form-wrapper {
position: absolute;
  top: 420px;         /* 약간 아래로 */
  left: 8%;
  max-width: 650px;   /* 최대 너비 증가 */
  width: 50%;
  background: rgba(255, 255, 255, 0.95); /* 조금 더 불투명 */
  padding: 24px 30px; /* 패딩 살짝 증가 */
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.18);
  z-index: 10;
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 5칸 균등 분할 */
  gap: 16px 20px; /* 세로 16px, 가로 20px 간격 */
  align-items: end;
}

.package-form-wrapper form {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  width: 100%;
  align-items: center;
  display: contents;
}

.form-group {
  display: flex;
  flex-direction: column;
  font-size: 1rem;
  min-width: 140px;
}

.form-group label {
  margin-bottom: 6px;
  font-weight: 600;
  color: #333;
}

.form-group select,
.form-group input[type='date'],
.form-group input[type='number'] {
  padding: 8px 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-group select:focus,
.form-group input[type='date']:focus,
.form-group input[type='number']:focus {
  border-color: #4A90E2;
  box-shadow: 0 0 6px rgba(74, 144, 226, 0.5);
}

/* form-row: 종료날짜, 인원수, 버튼 가로 배치 */
.form-row {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  width: 100%;
}

/* 인원수 input 너비 축소 */
.small-input input[type='number'] {
  width: 60px;
  padding: 6px 8px;
}

/* 조회 버튼 작게 */
.search-btn.small-btn {
  padding: 8px 20px;
  font-size: 0.9rem;
  background-color: #292e4c;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  height: 38px;
  white-space: nowrap; /* ✅ 줄바꿈 방지 */
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1; /* ✅ 글자가 위아래로 찌그러지는 것 방지 */
  margin-left: -55px; 
}

.search-btn.small-btn:hover {
  background-color: #39405e;
}

.recommended-section {
  padding: 40px 0; /* ✅ 좌우 여백 제거 */
  background-color: #f9f9f9;
  margin-top: 60px;
  text-align: center;
}

.recommended-section h2 {
  font-size: 2rem;
  margin-bottom: 24px;
  color: #333;
}

.recommended-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* ✅ 무조건 5개 */
  gap: 24px;
  width: 1200px; /* ✅ 5개 카드 * 평균 220~240px + 여백 */
  margin: 0 auto; /* ✅ 중앙 정렬 */
}
.recommended-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.3s ease;
}

.recommended-card:hover {
  transform: translateY(-5px);
}

.recommended-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.recommended-info {
  padding: 12px;
}

.recommended-info h4 {
  font-size: 1.1rem;
  margin-bottom: 4px;
}

.recommended-info p {
  font-size: 0.9rem;
  color: #666;
  margin: 2px 0;
}

.recommended-info .price {
  color: #4A90E2;
  font-weight: bold;
  margin-top: 8px;
}

.fixed-chat-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  background-color: #292e4c;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 999;
  transition: background 0.3s ease, box-shadow 0.3s ease, transform 0.2s ease;
  transform-origin: center center; /* ✅ 기준점 고정 */
}

.fixed-chat-button:hover {
  filter: brightness(1.5);
  transform: scale(1.08); /* ✅ 살짝 커지게 */
}

.fixed-chat-button img {
  width: 45px;
  padding: 5px 0 0 0;
}

.fixed-chat-wrapper {
  position: fixed;
  bottom: 90px;
  right: 15px;
  text-align: center;
  z-index: 999;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.chat-hint-text {
  font-size: 0.75rem;
  color: #333;
  background: #fff;
  padding: 5px 12px;
  border-radius: 15px; /* ▶ 오른쪽 말풍선 */
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  position: relative;
  white-space: nowrap;
}

/* 말풍선 꼬리 - 오른쪽 위 방향 */
.chat-hint-text::after {
  content: '';
  position: absolute;
  top: 100%;
  right: 12px;
  width: 0;
  height: 0;
  border-top: 6px solid #fff;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
}

</style>