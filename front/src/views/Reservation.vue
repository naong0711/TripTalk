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
      <div
        class="hotel-card"
        v-for="hotel in hotels"
        :key="hotel.id"
      >
        <img :src="hotel.image" alt="hotel photo" />
        <div class="hotel-info">
          <h3>{{ hotel.name }}</h3>
          <p>{{ hotel.location }}</p>
          <p>{{ checkIn }} ~ {{ checkOut }} · 성인 {{ adults }}명</p>
        </div>
        <div class="hotel-price">
          <p>{{ hotel.pricePerNight }}원 / 박</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const checkIn = ref(new Date().toISOString().slice(0, 10));
const checkOut = ref('');
const adults = ref(2);

const hotels = ref([
  {
    id: 1,
    name: '우연 한동 프라이빗 펜션',
    location: '제주도 한림읍',
    image: 'https://example.com/your-image.jpg',
    pricePerNight: '120,000',
  },
  {
    id: 2,
    name: '제주 바다펜션',
    location: '서귀포시',
    image: 'https://example.com/another-image.jpg',
    pricePerNight: '90,000',
  },
]);

function searchHotels() {
  console.log(`검색 → ${checkIn.value} ~ ${checkOut.value}, 인원: ${adults.value}`);
}
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
