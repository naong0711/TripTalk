<template>
    

  <div class="map-container" style="width: 100%; height: 500px;">
    <div id="map" style="width: 100%; height: 100%;"></div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

let map = null

onMounted(async () => {
  // window.kakao.maps가 로드될 때까지 기다림
  await waitForKakao()

  // kakao SDK 내부 지도 초기화 함수 호출
  window.kakao.maps.load(() => {
    initMap()
  })
})

function waitForKakao() {
  return new Promise((resolve) => {
    if (window.kakao && window.kakao.maps) {
      resolve()
    } else {
      const interval = setInterval(() => {
        if (window.kakao && window.kakao.maps) {
          clearInterval(interval)
          resolve()
        }
      }, 100)
    }
  })
}

function initMap() {
  const mapContainer = document.getElementById('map')
  const mapOption = {
    center: new kakao.maps.LatLng(37.566826, 126.9786567),
    level: 3,
  }
  map = new kakao.maps.Map(mapContainer, mapOption)
}
</script>

<style scoped>
.map-container {
  /* width와 height는 inline style로 설정했지만 필요시 여기서도 설정 가능 */
}
</style>
