<template>
  <div class="cart-container">
    <div class="cart-box-container">
      <h2>장바구니</h2>

      <div v-for="category in categories" :key="category" class="cart-section">
        <div class="section-header">
          <h3>{{ category }}</h3>
          <span class="total-count">총 {{ categorizedItems[category].length }}개</span>
        </div>
        <hr />

        <div v-if="categorizedItems[category].length === 0" class="empty-message">
          {{ category }} 카테고리에 담긴 상품이 없습니다.
        </div>

        <div v-else>
          <div
            class="cart-box"
            v-for="item in pagedItems(category)"
            :key="item.id"
          >
            <input type="checkbox" v-model="selectedItems" :value="item.id" />
            <img :src="item.thumbnailUrl || '/img/no-image.jpg'" class="cart-img" />

            <div class="cart-info" @click="goToProduct(item.productId)">
              <h3 class="title">{{ item.title }}</h3>
              <p class="address">{{ item.address }}</p>
              <p class="details">
                {{ formatDate(item.startDate) }} ~ {{ formatDate(item.endDate) }} 
              </p>
              <p class="price">{{ formatPrice(item.price) }}원</p>
            </div>

            <button class="delete-btn" @click="removeItem(item.id)">✕</button>
          </div>

          <!-- 페이징 -->
          <div
            v-if="totalPages(category) > 1"
            class="pagination"
          >
            <button
              :disabled="pageIndex[category] === 1"
              @click="changePage(category, pageIndex[category] - 1)"
              aria-label="이전 페이지"
            >
              ‹
            </button>
            <span>{{ pageIndex[category] }} / {{ totalPages(category) }}</span>
            <button
              :disabled="pageIndex[category] === totalPages(category)"
              @click="changePage(category, pageIndex[category] + 1)"
              aria-label="다음 페이지"
            >
              ›
            </button>
          </div>
        </div>
      </div>

      <!-- 결제 버튼 -->
      <div class="payment-area">
        <button
          class="payment-btn"
          :disabled="selectedItems.length === 0"
          @click="proceedPayment"
        >
          선택 상품 결제하기 ({{ selectedItems.length }}개)
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const cartItems = ref([])
const selectedItems = ref([])

const categoryMap = {
  1: '숙박',
  2: '교통',
  3: '레저'
}
const categories = ['숙박', '교통', '레저']

const pageIndex = reactive({
  숙박: 1,
  교통: 1,
  레저: 1
})

const ITEMS_PER_PAGE = 3

onMounted(async () => {
  try {
    const res = await axios.get('/api/mypage/cartList', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    cartItems.value = res.data
  } catch (err) {
    console.error('장바구니 불러오기 실패:', err)
  }
})

const categorizedItems = computed(() => {
  const result = { 숙박: [], 교통: [], 레저: [] }
  cartItems.value.forEach((item) => {
    const categoryName = categoryMap[item.categoryId]
    if (result[categoryName]) {
      result[categoryName].push(item)
    }
  })
  return result
})

function pagedItems(category) {
  const allItems = categorizedItems.value[category] || []
  const start = (pageIndex[category] - 1) * ITEMS_PER_PAGE
  return allItems.slice(start, start + ITEMS_PER_PAGE)
}

function totalPages(category) {
  return Math.ceil((categorizedItems.value[category]?.length || 0) / ITEMS_PER_PAGE) || 1
}

function changePage(category, newPage) {
  if (newPage < 1) return
  const maxPage = totalPages(category)
  if (newPage > maxPage) return
  pageIndex[category] = newPage
}

function goToProduct(productId) {
  router.push(`/product/${productId}`)
}

function formatPrice(price) {
  return price?.toLocaleString() ?? '0'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  return `${yyyy}.${mm}.${dd}`
}

async function removeItem(cartId) {
      const confirmed = window.confirm('장바구니에서 삭제하시겠습니까?');
  if (!confirmed) return;

  try {
    await axios.delete(`/api/mypage/cart/${cartId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    cartItems.value = cartItems.value.filter((item) => item.id !== cartId)
    categories.forEach((category) => {
      const maxPage = totalPages(category)
      if (pageIndex[category] > maxPage) {
        pageIndex[category] = maxPage
      }
    })
  } catch (err) {
    console.error('삭제 실패:', err)
    alert('삭제 중 문제가 발생했습니다.')
  }
}

function proceedPayment() {
  if (selectedItems.value.length === 0) {
    alert('결제할 상품을 선택해주세요.')
    return
  }
  // 실제 결제 이동 처리
  alert(`선택한 ${selectedItems.value.length}개 상품 결제로 이동합니다.`)
}
</script>

<style scoped>
/* 기존 스타일 유지 */
.cart-container {
  max-width: 720px;
  margin: 40px auto;
  font-family: 'Noto Sans KR', sans-serif;
}
.cart-box-container {
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
.cart-section {
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
.cart-box {
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
.cart-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border: 1px solid #ccc;
  border-radius: 8px;
}
.cart-info {
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
.details {
  font-size: 13px;
  color: #555;
  margin-bottom: 4px;
}
.price {
  font-size: 15px;
  font-weight: 700;
  color: #222;
}
.delete-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 4px;
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

/* 결제 버튼 영역 */
.payment-area {
  margin-top: 24px;
  text-align: center;
}

.payment-btn {
  background-color: #292e4c;
  border: none;
  color: white;
  font-size: 18px;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.payment-btn:disabled {
  background-color: #ddd;
  cursor: not-allowed;
}

.payment-btn:not(:disabled):hover {
  background-color: #1d2138;
}
</style>
