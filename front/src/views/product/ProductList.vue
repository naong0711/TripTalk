<template>
  <div class="product-page">
    
    <h2>여행 예약</h2>
          
    <div class="top-bar">
      <!-- ✅ 검색 바 -->
      <div class="search-bar">
        <div class="search-item">
            <label for="region">지역 선택</label>
            <select id="region" v-model="selectedRegion" required>
              <option disabled value="">지역을 선택하세요</option>
              <option v-for="(region, i) in regions" :key="i" :value="region">{{ region }}</option>
            </select>
        </div>
        <div class="search-item">
          <label>체크인</label>
          <input type="date" v-model="checkIn" />
        </div>
        <div class="search-item">
          <label>체크아웃</label>
          <input type="date" v-model="checkOut" />
        </div>
        <div class="search-item numeric">
          <label>인원수</label>
          <input type="number" v-model.number="adults" min="1" />
        </div>
        <button @click="searchProducts">검색</button>
      </div>
        <!-- ✅ 상단 등록 버튼 -->
       <div class="header-actions" v-if="isSeller">
         <button @click="goToRegister" class="go-register">상품 등록</button>
       </div>

    </div>

    <!-- ✅ 상품 목록 -->
    <div class="results">
      <div
        class="product-card"
        v-for="product in products"
        :key="product.id"
        @click="goToDetail(product.id)"
      >
        <img :src="product.image" alt="product photo" />
        <div class="product-info">
          <div class="title-with-like">
            <h3>{{ product.title }}</h3>
            <button 
              v-if="isFavoritesReady"
              :class="['favorite-btn', isFavorited(product.id) ? 'liked' : '']" 
              @click.stop="toggleFavorite(product.id)"
              aria-label="찜하기 버튼"
            >
              <span v-if="isFavorited(product.id)">❤️</span>
              <span v-else>🤍</span>
            </button>
          </div>
          
          <p>{{ product.address }}</p>
          <p>{{ product.startDate }} ~ {{ product.endDate }} </p>
          <p>인원수: {{ product.minPeople }} ~ {{ product.maxPeople }}명</p>
        </div>
        <div class="product-price">
          <p>{{ product.price.toLocaleString() }}원 / 박</p>
        </div>
      </div>
    </div>
  </div>

  <div class="pagination">
    <button @click="goToPage(page - 1)" :disabled="page === 0">이전</button>
    <span>{{ page + 1 }} / {{ totalPages }}</span>
    <button @click="goToPage(page + 1)" :disabled="page + 1 >= totalPages">다음</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const isSeller = ref(false)
const favorites = ref(new Set())
const favoriteMap = ref({})
const isFavoritesReady = ref(false)

const regions = ref([])
const selectedRegion = ref('')

function isFavorited(productId) {
  return favorites.value.has(productId)
}

async function toggleFavorite(productId) {
  if (!isFavoritesReady.value) return

  try {
    const token = localStorage.getItem('accessToken')
    if (!token) return

    if (favorites.value.has(productId)) {
      const favoriteId = favoriteMap.value[productId]
      if (!favoriteId) throw new Error('favoriteId not found for productId: ' + productId)

      await axios.delete(`/api/mypage/favorite/${favoriteId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })

      favorites.value.delete(productId)
      favorites.value = new Set(favorites.value)

      delete favoriteMap.value[productId]
      favoriteMap.value = { ...favoriteMap.value }
    } else {
      const res = await axios.post(`/api/mypage/favorite/${productId}`, null, {
        headers: { Authorization: `Bearer ${token}` }
      })

      favorites.value.add(productId)
      favorites.value = new Set(favorites.value)

      favoriteMap.value = { ...favoriteMap.value, [productId]: res.data.favoriteId }
    }
  } catch (e) {
    console.error('찜 처리 실패:', e)
  }
}

async function fetchFavorites() {
  try {
    isFavoritesReady.value = false

    const token = localStorage.getItem('accessToken')
    if (!token) return

    const res = await axios.get('/api/mypage/favoriteList', {
      headers: { Authorization: `Bearer ${token}` }
    })

    const favSet = new Set()
    const favMap = {}
    res.data.forEach(fav => {
      if (fav.productId && fav.id) {
        const pid = Number(fav.productId)
        favSet.add(pid)
        favMap[pid] = fav.id
      }
    })

    favorites.value = favSet
    favoriteMap.value = favMap
    isFavoritesReady.value = true
  } catch (e) {
    console.error('찜 목록 조회 실패:', e)
  }
}

const checkIn = ref(route.query.checkIn || new Date().toISOString().slice(0, 10))
const checkOut = ref(route.query.checkOut || new Date().toISOString().slice(0, 10))
const adults = ref(Number(route.query.people) || 2)

const products = ref([])
const totalPages = ref(0)
const page = ref(0)
const size = 9

// 전체 상품 목록 조회 함수
const fetchAllProducts = async () => {
  try {
    const res = await axios.get('/api/product', {
      params: {
        page: page.value + 1, // API가 1부터 페이지 시작 시
        size,
        sort: 'id,desc'
      }
    })
    const data = res.data
    products.value = (data.content || []).map(p => ({
      id: p.id,
      title: p.title,
      address: p.address || '',
      price: p.price,
      minPeople: p.minPeople || 1,
      maxPeople: p.maxPeople || 1,
      image: `/api/files/image/product/${p.id}`,
      startDate: p.startDate || '',
      endDate: p.endDate || ''
    }))
    totalPages.value = data.totalPages || 0
  } catch (err) {
    console.error('전체 상품 목록 불러오기 실패:', err)
  }
}

// 검색 조건에 따른 상품 검색 함수
const fetchSearchProducts = async () => {
  try {
    // 검색 조건이 하나라도 없으면 검색하지 않음 (필요에 따라 조건 조절 가능)
    const hasSearchParams = checkIn.value && checkOut.value && adults.value
    if (!hasSearchParams) {
      // 조건 없으면 전체 목록 호출
      await fetchAllProducts()
      return
    }

    const params = {
      ...(selectedRegion.value ? { location: selectedRegion.value } : {}),
      checkIn: typeof checkIn.value === 'string' ? checkIn.value : new Date(checkIn.value).toISOString().slice(0, 10),
      checkOut: typeof checkOut.value === 'string' ? checkOut.value : new Date(checkOut.value).toISOString().slice(0, 10),
      people: adults.value,
      page: page.value,
      size
    }

    const res = await axios.get('/api/product/search', { params })
    const data = res.data

    if (Array.isArray(data)) {
      products.value = data.map(p => ({
        id: p.id,
        title: p.title,
        address: p.address || '',
        price: p.price,
        minPeople: p.minPeople || 1,
        maxPeople: p.maxPeople || 1,
        image: `/api/files/image/product/${p.id}`,
        startDate: p.startDate || '',
        endDate: p.endDate || ''
      }))
      totalPages.value = 1
    } else {
      products.value = (data.content || []).map(p => ({
        id: p.id,
        title: p.title,
        address: p.address || '',
        price: p.price,
        minPeople: p.minPeople || 1,
        maxPeople: p.maxPeople || 1,
        image: `/api/files/image/product/${p.id}`,
        startDate: p.startDate || '',
        endDate: p.endDate || ''
      }))
      totalPages.value = data.totalPages || 0
    }
  } catch (err) {
    console.error('검색 상품 불러오기 실패:', err)
  }
}

const goToPage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    // 페이지 변경시 검색 조건이 있으면 검색, 아니면 전체 조회
    if (checkIn.value && checkOut.value && adults.value) {
      fetchSearchProducts()
    } else {
      fetchAllProducts()
    }
  }
}

const fetchRegions = async () => {
  try {
    const res = await axios.get('/api/product/locations')
    regions.value = res.data
  } catch (e) {
    console.error('지역 목록 불러오기 실패:', e)
  }
}

const searchProducts = () => {
  page.value = 0
  fetchSearchProducts()
}

const goToDetail = (id) => {
  router.push({
    path: `/product/${id}`,
    query: {
      checkIn: checkIn.value,
      checkOut: checkOut.value,
      adults: adults.value
    }
  })
}

const goToRegister = () => {
  router.push('/productRegister')
}

const fetchIsSeller = async () => {
  try {
    const response = await axios.get('/api/sellers/my-id', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })

    if (response.data > 0) {
      isSeller.value = true
    }
    console.log('📦 sellerId:', response.data)
  } catch (err) {
    isSeller.value = false
  }
}

onMounted(() => {
  fetchAllProducts()
  fetchIsSeller()
  fetchFavorites()
  fetchRegions()
})
</script>


<style scoped>
h2 {
  margin-bottom: 10px;
  padding-bottom: 20px;
  padding-top: 5px;
  border-bottom: 2px solid #333;
}

.product-page {
  padding: 24px;
  max-width: 960px;
  margin: auto;
}
.header-actions {
  text-align: right;
  margin-bottom: 16px;
}
.go-register {
  padding: 8px 16px;
  background-color: #5cb85c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.go-register:hover {
  background-color: #449d44;
}
.search-bar {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 24px;
}
.search-item {
  display: flex;
  flex-direction: column;
}
.search-item.numeric input {
  width: 60px;
}
.results {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.product-card {
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}
.product-card img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}
.product-info {
  padding: 12px;
  flex: 1;
}
.product-price {
  padding: 12px;
  font-weight: bold;
  text-align: right;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
  gap: 12px;
}
.pagination button {
  padding: 6px 12px;
  background-color: #f1f1f1;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}
.pagination button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}
.pagination span {
  font-weight: bold;
}
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}
.search-item input[type="date" i],
.search-item input[type='number'],
.search-item select{
  padding: 8px 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  outline: none;
  transition: border-color 0.3s ease;
}
.search-bar button {
  padding: 8px 20px;
  font-size: 0.9rem;
  background-color: #292e4c;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  height: 38px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}
.search-bar button:hover {
  background-color: #39405e;
}

.title-with-like {
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-with-like h3 {
  margin: 0;
  flex-grow: 1;
}

.favorite-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #bbb;
  transition: color 0.3s ease;
  padding: 0;
  line-height: 1;
  user-select: none;
  flex-shrink: 0;
}

.favorite-btn:hover {
  color: #e63946;
}

.favorite-btn.liked {
  color: #e63946;
}
</style>
