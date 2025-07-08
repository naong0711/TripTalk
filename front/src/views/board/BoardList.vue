<template>
  <div class="board-page">
    <!-- 상단 버튼 -->
    <div class="header-actions">
      <button @click="goToWrite" class="write-btn">✍ 후기 작성</button>
    </div>

    <!-- 게시판 리스트 -->
    <div class="board-table">
      <div class="board-header">
        <span class="col-title">제목</span>
        <span class="col-writer">작성자</span>
        <span class="col-date">작성일</span>
      </div>

      <div
        class="board-row"
        v-for="log in logs"
        :key="log.id"
        @click="goToDetail(log.id)"
      >
        <span class="col-title">{{ log.title }}</span>
        <span class="col-writer">{{ log.nickname }}</span>
        <span class="col-date">{{ formatDate(log.createdAt) }}</span>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination">
      <button @click="goToPage(page - 1)" :disabled="page === 0">이전</button>
      <span>{{ page + 1 }} / {{ totalPages }}</span>
      <button @click="goToPage(page + 1)" :disabled="page + 1 >= totalPages">다음</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const logs = ref([])
const page = ref(0)
const size = 10
const totalPages = ref(0)

const fetchLogs = async () => {
  try {
    const res = await axios.get('/api/log/list', {
      params: {
        page: page.value,
        size: size,
        sortBy: 'id',
        direction: 'desc'
      }
    })
    logs.value = res.data.content
    totalPages.value = res.data.totalPages
  } catch (err) {
    console.error('후기 목록 조회 실패:', err)
  }
}
const goToDetail = (id) => {
  router.push(`/boardDetail/${id}`)
}

const goToWrite = () => {
  router.push('/boardWrite')
}

const goToPage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    fetchLogs()
  }
}

const formatDate = (str) => {
  return new Date(str).toLocaleDateString()
}

onMounted(fetchLogs)

</script>

<style scoped>
.board-page {
  max-width: 900px;
  margin: 30px auto;
  font-family: 'Apple SD Gothic Neo', sans-serif;
}

.header-actions {
  text-align: right;
  margin-bottom: 16px;
}

.write-btn {
  background-color: #4a90e2;
  color: #fff;
  padding: 8px 14px;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.write-btn:hover {
  background-color: #357abd;
}

.board-table {
  width: 100%;
  border-top: 2px solid #333;
}

.board-header,
.board-row {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid #ddd;
  align-items: center;
}

.board-header {
  background-color: #f8f8f8;
  font-weight: bold;
  font-size: 15px;
}

.board-row {
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.board-row:hover {
  background-color: #f0f7ff;
}

.col-title {
  flex: 5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.col-writer {
  flex: 2;
  text-align: center;
}

.col-date {
  flex: 2;
  text-align: right;
  color: #888;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
  gap: 16px;
}

.pagination button {
  padding: 6px 12px;
  border: none;
  background-color: #1976d2;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
