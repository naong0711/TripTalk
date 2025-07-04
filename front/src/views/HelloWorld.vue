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
              <option v-for="(region, i) in backgroundTexts" :key="i" :value="region">{{ region }}</option>
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
</template>

<script setup>
import 'swiper/css'
import 'swiper/css/pagination'
import 'swiper/css/autoplay'

import { Swiper, SwiperSlide } from 'swiper/vue'
import { Pagination, Autoplay } from 'swiper/modules'
import { ref } from 'vue'

const modules = [Pagination, Autoplay]
const swiperRef = ref(null)
const swiperInstance = ref(null)

const onSwiperInit = (swiper) => {
  swiperInstance.value = swiper
}

const selectedRegion = ref('')
const startDate = ref('')
const endDate = ref('')
const peopleCount = ref(1)

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
  'linear-gradient(135deg, #6DD5FA, #4A9FF4)',
  'linear-gradient(135deg, #FFDD95, #FBB464)',
  'linear-gradient(135deg, #28C76F, #83FCBF)',
  'linear-gradient(135deg, #FFB6B9, #FF9AA2)',
  'linear-gradient(135deg, #C3F2E3, #B5EAD7)',
  'linear-gradient(135deg, #A0A8F5, #C7CEEA)',
  'linear-gradient(135deg, #F9BFAE, #FFDAC1)'
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

function onSearch() {
  alert(`지역: ${selectedRegion.value}\n출발일: ${startDate.value}\n종료일: ${endDate.value}\n인원수: ${peopleCount.value}명`)
}

const goPrev = () => {
  swiperInstance.value?.slidePrev()
}
const goNext = () => {
  swiperInstance.value?.slideNext()
}
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
  top: 40px;
  left: 8%;
  font-size: 6rem;
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
  max-width: 400px;
}

.swiper-inner {
  width: 40%;
  height: 40%;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  overflow: visible;
  position: relative;
  margin-left: 1000px;
  margin-top: auto;
  z-index: 1;
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
  top: 400px;
  left: 8%;
  max-width: 600px;
  width: 45%;
  background: rgba(255, 255, 255, 0.9);
  padding: 18px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.package-form-wrapper form {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  width: 100%;
  align-items: center;
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
  padding: 8px 12px;
  font-size: 0.9rem;
  background-color: #4A90E2;
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
  background-color: #357ABD;
}
</style>