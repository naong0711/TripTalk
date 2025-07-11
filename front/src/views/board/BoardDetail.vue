<template>
  <div class="detail-wrapper" v-if="log">
    <div class="detail-box">
      <h2 class="title">{{ log.title }}</h2>
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
        <button @click="goToEdit" v-if="isOwner">✏ 수정</button>
        <button @click="deletePost" v-if="isOwner">🗑 삭제</button>
        <button @click="goBack">← 목록으로</button>
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
}
.title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #222;
}
.meta {
  font-size: 14px;
  color: #777;
  margin-bottom: 24px;
}
.meta .bold {
  font-weight: bold;
  color: #444;
}
.meta .date {
  font-style: italic;
}
.content {
  font-size: 18px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 32px;
  white-space: pre-wrap;
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #eee;
}
.map-container {
  width: 100%;
  height: 400px;
  margin-bottom: 32px;
}
.actions {
  text-align: right;
}
.actions button {
  margin-left: 8px;
  padding: 8px 14px;
  font-size: 14px;
  border: none;
  background-color: #4a90e2;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.actions button:hover {
  background-color: #357ab7;
}
</style>
