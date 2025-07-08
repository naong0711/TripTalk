  <template>
    <div class="product-page">
      <!-- ✅ 상단 등록 버튼 -->
      <div class="header-actions">
        <button @click="goToRegister" class="go-register">상품 등록</button>
      </div>

      <!-- ✅ 검색 바 -->
      <div class="search-bar">
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
            <h3>{{ product.title }}</h3>
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


function toValidDate(dateStr) {
  const date = new Date(dateStr)
  return isNaN(date.getTime()) ? new Date() : date
}

const checkIn = ref(route.query.checkIn || new Date().toISOString().slice(0, 10))
const checkOut = ref(route.query.checkOut || new Date().toISOString().slice(0, 10))
const adults = ref(Number(route.query.people) || 2)

const products = ref([])
const totalPages = ref(0)
const page = ref(0)       // ✅ 프론트는 0부터 시작
const size = 9            // ✅ 9개씩 보여주기


const location = route.query.location
const people = parseInt(route.query.people || 1) //검색 서치 임시
const fetchSearchProducts = async () => {
  try {
    const hasSearchParams = location && checkIn.value && checkOut.value && adults.value

    if (hasSearchParams) {
      const params = {
        location,
        checkIn: typeof checkIn.value === 'string' ? checkIn.value : new Date(checkIn.value).toISOString().slice(0, 10),
        checkOut: typeof checkOut.value === 'string' ? checkOut.value : new Date(checkOut.value).toISOString().slice(0, 10),
        people: adults.value,
        page: page.value,
        size: size || 9
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
    } else {
      // 전체 상품 목록 호출
      const res = await axios.get('/api/product', {
        params: {
          page: page.value + 1,
          size: size || 9,
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
    }
  } catch (err) {
    console.error('상품 검색 실패:', err)
  }
}


const goToPage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    fetchProducts()
  }
}

const searchProducts = () => {
  page.value = 0

  fetchSearchProducts()  // ✅ 이거로 교체
}
const goToDetail = (id) => {
  router.push({
    path: `/productDetail/${id}`,
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

onMounted(() => {
  fetchSearchProducts()
})


</script>

  <style scoped>
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
  .search-bar button {
    padding: 10px 20px;
    background-color: #4A90E2;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  .search-bar button:hover {
    background-color: #357ABD;
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
  </style>
