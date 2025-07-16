<template>
  <div class="detail-wrapper" v-if="log">
    <div class="detail-box">
      <div class="title-wrapper">
        <a class="back-btn" @click="goBack">&lt;</a>
        <h2 class="title">{{ log.title }}</h2>
      </div>
      <div class="meta">
        작성자 ID: <span class="bold">{{ log.userId }}</span> |
        작성일: <span class="date">{{ formatDate(log.createdAt) }}</span>
      </div>

      <div class="content">
        {{ log.content }}
      </div>

      <!-- ✅ 지도 출력 -->
      <div id="map" class="map-container"></div>

      <div class="actions">
        <button @click="goToEdit" v-if="isOwner">수정</button>
        <button @click="deletePost" v-if="isOwner">삭제</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const log = ref(null)
const currentUserId = ref(null)
const isOwner = ref(false)

let map = null
let polyline = null
let markers = []
const boardId = route.params.id


const fetchLog = async () => {
  try {
    const res = await axios.get(`/api/log/list/${route.params.id}`)
    log.value = res.data
    checkOwnership()

    if (window.kakao && log.value?.id) {
      window.kakao.maps.load(() => {
        loadMapAndBookmarks(log.value.id)
      })
    }
  } catch (err) {
    console.error('후기 조회 실패:', err)
  }
}

const fetchCurrentUser = async () => {
  try {
    const res = await axios.get('/api/user/me', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    currentUserId.value = res.data.id
    checkOwnership()
  } catch (err) {
    console.warn('로그인 정보 없음 (비로그인 사용자)', err)
  }
}

const checkOwnership = () => {
  if (log.value && currentUserId.value) {
    isOwner.value = log.value.userId === currentUserId.value
  }
}

const deletePost = async () => {
  if (confirm('정말 삭제하시겠습니까?')) {
    try {
      await axios.delete(`/api/log/delete/${route.params.id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      })
      router.push('/boardlist')
    } catch (err) {
      alert('삭제 실패')
      console.error(err)
    }
  }
}

const goToEdit = () => {
  router.push({ path: '/boardWrite', query: { mode: 'edit', id: route.params.id } })
}

const goBack = () => {
  router.push('/boardlist')
}

const formatDate = (str) => new Date(str).toLocaleString()

// ✅ 지도 및 북마크 마커 + 선 표시
const loadMapAndBookmarks = async (id) => {
  try {
    console.log(id)
    const res = await axios.get(`/api/map/get/${id}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })

    const bookmarks = res.data

    const container = document.getElementById('map')
    map = new window.kakao.maps.Map(container, {
      center: new window.kakao.maps.LatLng(37.5665, 126.9780),
      level: 5,
    })

    const path = []
    bookmarks.forEach((bookmark, idx) => {
      const position = new window.kakao.maps.LatLng(bookmark.latitude, bookmark.longitude)

      const marker = new window.kakao.maps.Marker({
        map,
        position,
        title: bookmark.placeName,
      })

      markers.push(marker)
      path.push(position)
    })

    if (polyline) polyline.setMap(null)
    polyline = new window.kakao.maps.Polyline({
      path,
      strokeWeight: 5,
      strokeColor: '#FF0000',
      strokeOpacity: 0.7,
      strokeStyle: 'solid',
    })
    polyline.setMap(map)

    const bounds = new window.kakao.maps.LatLngBounds()
    path.forEach(pos => bounds.extend(pos))
    map.setBounds(bounds)
  } catch (err) {
    console.error('지도 로딩 실패:', err)
  }
}

onMounted(() => {
  console.log(boardId)
  fetchCurrentUser()
  fetchLog()
})
</script>

<style scoped>
.detail-wrapper {
  background-color: #f6f6f6;
  padding: 32px;
  min-height: 100vh;
}
.detail-box {
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 12px;
  max-width: 800px;
  margin: 0 auto;
  padding: 32px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.06);
  font-family: 'Noto Sans KR', sans-serif;
  color: #2c2c2c;
}
.title-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.back-btn {
  cursor: pointer;
  font-size: 24px;
  font-weight: 700;
  user-select: none;
  color: #292e4c;
  text-decoration: none;
  padding: 0 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}
.back-btn:hover {
  background-color: #e1e6f9;
}
.title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #292e4c;
}
.meta {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 24px;
}
.meta .bold {
  font-weight: 700;
  color: #444;
}
.meta .date {
  font-style: italic;
  color: #444;
}
.content {
  font-size: 16px;
  color: #333;
  line-height: 1.7;
  margin-bottom: 32px;
  white-space: pre-wrap;
  background: #fafbff;
  padding: 16px;
  border-radius: 8px;
  border: 1.5px solid #d1d5db;
  box-shadow: inset 0 1px 3px rgb(0 0 0 / 0.1);
}
.map-container {
  width: 100%;
  height: 400px;
  margin-bottom: 32px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(41, 46, 76, 0.15);
  border: 1.5px solid #c9cedb;
}
.actions {
  text-align: right;
}
.actions button {
  margin-left: 8px;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  color: #fff;
  background-color: #6b7280; /* 기본 회색 (목록 버튼용) */
  box-shadow: 0 2px 6px rgba(107, 114, 128, 0.3);
  transition: background-color 0.25s ease, box-shadow 0.25s ease;
}

.actions button:hover {
  background-color: #595f6d; /* 진한 회색 */
  box-shadow: 0 4px 12px rgba(75, 85, 99, 0.5);
}

/* 수정, 삭제 버튼은 연한 네이비 */
.actions button:first-child,
.actions button:nth-child(2) {
  background-color: #292e4c; /* 연한 네이비 */
  box-shadow: 0 2px 6px rgba(90, 106, 145, 0.4);
  color: #fff;
}

.actions button:first-child:hover,
.actions button:nth-child(2):hover {
  background-color: #1f2540; /* 좀 더 진한 네이비 */
  box-shadow: 0 4px 12px rgba(67, 83, 122, 0.6);
}
</style>
