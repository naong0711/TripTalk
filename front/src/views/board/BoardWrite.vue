<template>
  <div class="container">
    <!-- 왼쪽: 작성 폼 -->
    <div class="write-page">
      <h2>{{ isEditMode ? '후기 수정' : '후기 작성' }}</h2>

      <div class="form-group">
        <label>제목</label>
        <input type="text" v-model="form.title" />
      </div>

      <div class="form-group">
        <label>내용</label>
        <textarea v-model="form.content" rows="10" />
      </div>

      <input type="hidden" v-model="form.categoryId" />

      <div class="actions">
        <button class="btn submit" @click="submit">{{ isEditMode ? '수정' : '작성' }}</button>
        <button class="btn cancel" @click="goBack">취소</button>
      </div>
    </div>

    <!-- 오른쪽: 지도 컴포넌트 -->
    <div class="form-map">
      <KakaoMapSearch />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

import KakaoMapSearch from '@/components/KakaoMapSearch.vue'

const route = useRoute()
const router = useRouter()

const tempKey = crypto.randomUUID()
localStorage.setItem('bookmarkTempKey', tempKey)

const isEditMode = ref(route.query.mode === 'edit')
const form = ref({
  title: '',
  content: '',
  userId: null, // 로그인한 사용자 ID를 여기 세팅
  categoryId: 1
})

const fetchPost = async () => {
  if (isEditMode.value && route.query.id) {
    console.log('수정 모드 - 게시글 ID:', route.query.id)
    try {
      const res = await axios.get(`/api/log/list/${route.query.id}`)
      console.log('게시글 조회 응답:', res.data)
      const data = res.data
      form.value = {
        title: data.title,
        content: data.content,
        userId: data.userId,
        categoryId: 1 // 필요 시 데이터에서 실제 값 가져오기
      }
    } catch (err) {
      console.error('게시글 조회 실패:', err)
    }
  } else {
    console.log('작성 모드 또는 게시글 ID 없음')
  }
}

const fetchUserId = async () => {
  try {
    // 내 정보 조회 API (예: /api/user/me)
    const res = await axios.get('/api/user/me', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    form.value.userId = res.data.id
  } catch (err) {
    console.error('내 정보 조회 실패:', err)
    alert('사용자 정보를 가져오지 못했습니다. 로그인 상태를 확인하세요.')
  }
}


const submit = async () => {
  try {
    if (isEditMode.value) {
      await axios.put(`/api/log/update/${route.query.id}`, form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      })
      alert('수정 완료 되었습니다.')
      router.push('/boardlist')
    } else {
      // 1. 글 등록
      const res = await axios.post('/api/log/write', form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      })

      // 2. boardId 받아옴
      const boardIdCreated = res.data.id

      // 3. 북마크 연결 (tempKey → boardId)
      const tempKey = localStorage.getItem('bookmarkTempKey')
      if (tempKey) {
        await axios.put('/api/map/link', {
          boardId: boardIdCreated,
          tempKey
        }, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
          },
        })
        localStorage.removeItem('bookmarkTempKey')
      }

      alert('등록 완료!')
      router.push('/boardlist')
    }
  } catch (err) {
    console.error('등록/수정 실패:', err)
    alert('실패했습니다')
  }
}


const goBack = () => {
  router.push('/boardlist')
}

onMounted(() => {
  fetchUserId()
  fetchPost()
})
</script>


<style scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 24px;
  padding: 24px;
}

/* 작성 폼 스타일 */
.write-page {
  flex: 1;
  max-width: 600px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.write-page h2 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #555;
}

textarea,
input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
}

/* 버튼 그룹 */
.actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  font-size: 14px;
}

.btn.submit {
  background-color: #292e4c;
  color: white;
}

.btn.submit:hover {
  background-color: #292e4c;
  color: white;
}

.btn.cancel {
  background-color: #757575;
  color: white;
}

/* 지도 영역 */
.form-map {
  flex: 1;
  min-width: 480px;
  max-width: 720px;
}

/* 반응형 */
@media (max-width: 1024px) {
  .container {
    flex-direction: column;
    align-items: stretch;
  }
  .form-map {
    min-width: 100%;
  }
}

textarea,
input {
  width: 94%;
  padding: 12px 16px;
  border: 1.5px solid #d1d5db; /* 부드러운 연회색 (#d1d5db) */
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  box-shadow: inset 0 1px 3px rgb(0 0 0 / 0.1);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

textarea:focus,
input:focus {
  outline: none;
  border-color: #4caf50; /* 초록색 강조 */
  box-shadow: 0 0 8px rgba(76, 175, 80, 0.5);
  background-color: #fff;
}
</style>