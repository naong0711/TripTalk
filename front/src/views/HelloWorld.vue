<template>
  <div
    class="swiper-background"
    :style="{ background: currentBackground, transition: 'background 0.8s ease' }"
  >
    <div class="text-overlay">{{ currentText }}</div>

    <div class="swiper-inner">
      <Swiper
        :modules="modules"
        :slides-per-view="1"
        :space-between="30"
        :loop="true"
        :pagination="{ clickable: true }"
        :autoplay="{ delay: 10000, disableOnInteraction: false }"
        class="mySwiper"
        @slideChange="onSlideChange"
      >
        <SwiperSlide v-for="(img, index) in images" :key="index">
          <img :src="img" class="slide-image" />
        </SwiperSlide>
      </Swiper>
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

const images = [
  new URL('@/assets/busan.jpg', import.meta.url).href,
  new URL('@/assets/daejeon.jpg', import.meta.url).href,
  new URL('@/assets/seoul.jpg', import.meta.url).href
]

const backgrounds = [
  'linear-gradient(135deg, #d0e8ff, #a0c4ff)',  // Busan
  'linear-gradient(135deg, #ffe29a, #ff8a65)',  // Daejeon
  'linear-gradient(135deg, #d4fc79, #96e6a1)'   // Seoul
]

const backgroundTexts = ['Busan', 'Daejeon', 'Seoul']

const currentBackground = ref(backgrounds[0])
const currentText = ref(backgroundTexts[0])

function onSlideChange(swiper) {
  const realIndex = swiper.realIndex
  currentBackground.value = backgrounds[realIndex % backgrounds.length]
  currentText.value = backgroundTexts[realIndex % backgroundTexts.length]
}
</script>

<style scoped>
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

/* 글자 크기 더 크게 (10rem) */
.text-overlay {
  position: absolute;
  top: 40px;
  left: 8%;
  font-size: 10rem;
  font-weight: 900;
  color: rgba(0, 0, 0, 0.4); /* 진한 무채색 */
  user-select: none;
  pointer-events: none;
  z-index: 0;
}

.swiper-inner {
  width: 40%;
  height: 40%;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
  margin-left: auto;
  margin-top: auto;
  transform: translate(-15%, 20%);
  z-index: 1;
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: opacity 0.8s ease;
}

/* pagination 스타일 오버라이드 */
::v-deep(.swiper-pagination-bullets) {
  bottom: 10px;
}

::v-deep(.swiper-pagination-bullet) {
  background: #bbb;
  opacity: 1;
}

::v-deep(.swiper-pagination-bullet-active) {
  background: #2c3e50;
}
</style>