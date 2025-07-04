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
            <p>{{ checkIn }} ~ {{ checkOut }} · 성인 {{ adults }}명</p>
          </div>
          <div class="product-price">
            <p>{{ product.price.toLocaleString() }}원 / 박</p>
          </div>
        </div>
      </div>
    </div>
  </template>

  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'

  const router = useRouter()

  const checkIn = ref(new Date().toISOString().slice(0, 10))
  const checkOut = ref('')
  const adults = ref(2)

  const products = ref([])

  const fetchProducts = async () => {
    try {
      const response = await axios.get('/api/product')
      products.value = response.data.content.map(p => ({
        id: p.id,
        title: p.title,
        address: p.address,
        price: p.price,
        image: `/api/files/image/product/${p.id}`
      }))
    } catch (err) {
      console.error('상품 목록 로딩 실패:', err)
    }
  }

  const searchProducts = () => {
    console.log(`검색: ${checkIn.value} ~ ${checkOut.value}, 성인 ${adults.value}명`)
    // 추후 백엔드에 검색 조건 전송 예정
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
    fetchProducts()
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
  </style>
