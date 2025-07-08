<template>
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

    <!-- 작성자 ID 입력 삭제 또는 disabled 처리 -->
    <!--
    <div class="form-group">
      <label>작성자 ID</label>
      <input type="number" v-model="form.userId" disabled />
    </div>
    -->

    <div class="form-group">
      <label>카테고리 ID</label>
      <input type="number" v-model="form.categoryId" />
    </div>

    <div class="actions">
      <button @click="submit">{{ isEditMode ? '수정' : '작성' }}</button>
      <button @click="goBack">취소</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const isEditMode = ref(route.query.mode === 'edit')
const form = ref({
  title: '',
  content: '',
  userId: null, // 로그인한 사용자 ID를 여기 세팅
  categoryId: null
})

const fetchPost = async () => {
  if (isEditMode.value && route.query.id) {
    const res = await axios.get(`/api/log/list/${route.query.id}`)
    const data = res.data
    form.value = {
      title: data.title,
      content: data.content,
      userId: data.userId,
      categoryId: 1 // 기본값 또는 실제 데이터에서 가져오기
    }
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
    } else {
      await axios.post('/api/log/write', form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      })
    }
    router.push('/boardlist')
  } catch (err) {
    console.error('등록/수정 실패:', err)
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
.write-page {
  max-width: 720px;
  margin: auto;
  padding: 24px;
}
.form-group {
  margin-bottom: 16px;
}
textarea, input {
  width: 100%;
  padding: 8px;
}
.actions button {
  margin-right: 12px;
}
</style>