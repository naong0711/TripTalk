<template>
  <div class="favorite-container">
    <div class="favorite-box-container">
      <h2>찜 목록</h2>

      <div v-for="category in categories" :key="category" class="favorite-section">
        <div class="section-header">
          <h3>{{ category }}</h3>
          <span class="total-count">총 {{ categorizedItems[category].length }}개</span>
        </div>
        <hr />

        <div v-if="categorizedItems[category].length === 0" class="empty-message">
          {{ category }} 카테고리에 찜한 상품이 없습니다.
        </div>

        <div v-else>
          <div class="favorite-box" v-for="item in pagedItems(category)" :key="item.id">
            <img :src="item.thumbnailUrl || '/img/no-image.jpg'" class="favorite-img" />
            <div class="favorite-info" @click="goToProduct(item.productId)">
              <h3 class="title">{{ item.title }}</h3>
              <p class="address">{{ item.address }}</p>
              <p class="price">{{ formatPrice(item.price) }}원</p>
            </div>
            
            <!-- 삭제 버튼 추가 -->
            <button class="delete-btn" @click="removeFavorite(item.id)">✕</button>
          </div>

          <div v-if="totalPages(category) > 1" class="pagination">
            <button :disabled="pageIndex[category] === 1" @click="changePage(category, pageIndex[category] - 1)">‹</button>
            <span>{{ pageIndex[category] }} / {{ totalPages(category) }}</span>
            <button :disabled="pageIndex[category] === totalPages(category)" @click="changePage(category, pageIndex[category] + 1)">›</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const favoriteItems = ref([])

const categoryMap = {
  1: '숙박',
  2: '교통',
  3: '레저'
}
const categories = ['숙박', '교통', '레저']
const pageIndex = reactive({ 숙박: 1, 교통: 1, 레저: 1 })
const ITEMS_PER_PAGE = 3

onMounted(async () => {
  try {
    const res = await axios.get('/api/mypage/favoriteList', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    favoriteItems.value = res.data
  } catch (err) {
    console.error('찜 목록 불러오기 실패:', err)
  }
})

const categorizedItems = computed(() => {
  const result = { 숙박: [], 교통: [], 레저: [] }
  favoriteItems.value.forEach(item => {
    const categoryName = categoryMap[item.categoryId]
    if (result[categoryName]) result[categoryName].push(item)
  })
  return result
})

function pagedItems(category) {
  const start = (pageIndex[category] - 1) * ITEMS_PER_PAGE
  return categorizedItems.value[category].slice(start, start + ITEMS_PER_PAGE)
}

function totalPages(category) {
  return Math.ceil((categorizedItems.value[category]?.length || 0) / ITEMS_PER_PAGE) || 1
}

function changePage(category, newPage) {
  const max = totalPages(category)
  if (newPage >= 1 && newPage <= max) pageIndex[category] = newPage
}

function goToProduct(productId) {
  router.push(`/product/${productId}`)
}

function formatPrice(price) {
  return price?.toLocaleString() ?? '0'
}

// 찜 삭제 함수
async function removeFavorite(favoriteId) {
  const confirmed = window.confirm('찜 목록에서 삭제하시겠습니까?')
  if (!confirmed) return

  try {
    await axios.delete(`/api/mypage/favorite/${favoriteId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    // 삭제 후 목록에서 제거
    favoriteItems.value = favoriteItems.value.filter(item => item.id !== favoriteId)

    // 삭제 후 페이지 인덱스 조정 (옵션)
    categories.forEach(category => {
      const maxPage = totalPages(category)
      if (pageIndex[category] > maxPage) {
        pageIndex[category] = maxPage
      }
    })
  } catch (err) {
    console.error('찜 삭제 실패:', err)
    alert('삭제 중 문제가 발생했습니다.')
  }
}
</script>

<style scoped>
.favorite-container {
  max-width: 720px;
  margin: 40px auto;
  font-family: 'Noto Sans KR', sans-serif;
}

.favorite-box-container {
  padding: 24px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

h2 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 24px;
  color: #333;
}

.favorite-section {
  margin-bottom: 36px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  font-size: 18px;
  color: #444;
  margin-bottom: 8px;
}

.total-count {
  font-size: 14px;
  color: #666;
}

hr {
  border: none;
  border-top: 1px solid #ccc;
  margin-bottom: 16px;
}

.empty-message {
  text-align: center;
  color: #999;
  margin-bottom: 20px;
}

.favorite-box {
  display: flex;
  align-items: center;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 16px;
  background: #fafafa;
  gap: 12px;
  position: relative;
}

.favorite-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.favorite-info {
  flex: 1;
  cursor: pointer;
}

.title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
  color: #333;
}

.address {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.price {
  font-size: 15px;
  font-weight: 700;
  color: #c8ad7f;
}

.delete-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: color 0.2s;
}

.delete-btn:hover {
  color: red;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.pagination button {
  padding: 6px 12px;
  font-size: 14px;
  border: 1px solid #fff;
  background: white;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
