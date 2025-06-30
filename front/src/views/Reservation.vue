<template>
  <div id="app" class="search-page">
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
      <button @click="searchHotels">검색</button>
    </div>

    <div class="results">
      <router-link
        class="hotel-card"
        v-for="hotel in hotels"
        :key="hotel.id"
        :to="`/product/${hotel.id}`"
      >
        <img :src="hotel.image" alt="hotel photo" />
        <div class="hotel-info">
          <h3>{{ hotel.title }}</h3>
          <p>{{ hotel.address }}</p>
          <p>{{ checkIn }} ~ {{ checkOut }} · 성인 {{ adults }}명</p>
        </div>
        <div class="hotel-price">
          <p>{{ hotel.price.toLocaleString() }}원 / 박</p>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const checkIn = ref(new Date().toISOString().slice(0, 10))
const checkOut = ref('')
const adults = ref(2)
const hotels = ref([])

async function fetchHotels() {
  try {
    const response = await axios.get('/api/product')
    hotels.value = response.data.content.map(product => ({
      id: product.id,
      title: product.title,
      address: product.address,
      price: product.price,
      image: `/api/files/image/product/${product.id}`
    }))
  } catch (err) {
    console.error('숙소 목록 불러오기 실패:', err)
  }
}

function searchHotels() {
  console.log(`검색 → ${checkIn.value} ~ ${checkOut.value}, 인원: ${adults.value}`)
  // TODO: 검색 파라미터 API 연동 시 여기에 쿼리 파라미터 처리
}

onMounted(() => {
  fetchHotels()
})
</script>

<style scoped>
.search-page {
  padding: 24px;
  max-width: 960px;
  margin: auto;
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

.hotel-card {
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  text-decoration: none;
  color: inherit;
}

.hotel-card img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.hotel-info {
  padding: 12px;
  flex: 1;
}

.hotel-price {
  padding: 12px;
  font-weight: bold;
  text-align: right;
}
</style>
