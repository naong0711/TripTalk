<template>
  <div class="product-detail">
    <div class="image-section">
      <img :src="imageUrl" alt="상품 이미지" />
    </div>

    <div class="info-section">
      <h2>{{ product.title }}</h2>
      <p class="description">{{ product.description }}</p>
      <p><strong>주소:</strong> {{ product.address }}</p>

      <p><strong>가격:</strong> {{ product.price.toLocaleString() }}원 / 박</p>
      
      <p>
        <strong>시작일:</strong>
        {{ formatDate(product.startDate) }}
      </p>
      <p>
        <strong>종료일:</strong>
        {{ formatDate(product.endDate) }}
      </p>
      
      <p><strong>판매자 ID:</strong> {{ product.sellerId }}</p>
      <p><strong>카테고리 ID:</strong> {{ product.categoryId }}</p>

      <div v-if="product.discount">
        <h3>할인 정보</h3>
        <p><strong>할인 타입:</strong> {{ product.discount.discountType }}</p>
        <p><strong>할인 이름:</strong> {{ product.discount.name }}</p>
        <p><strong>할인율:</strong> {{ product.discount.discountRate }}%</p>
        <p><strong>할인 시작일:</strong> {{ formatDate(product.discount.startAt) }}</p>
        <p><strong>할인 종료일:</strong> {{ formatDate(product.discount.endAt) }}</p>
      </div>
    </div>

    <div class="booking-box">
      <label>체크인</label>
      <input type="date" v-model="checkIn" />

      <label>체크아웃</label>
      <input type="date" v-model="checkOut" />

      <label>성인</label>
      <input type="number" min="1" v-model.number="adults" />

      <label>결제 방법</label>
      <select v-model="paymentMethod">
        <option value="">선택하세요</option>
        <option value="CARD">신용카드</option>
        <option value="PAYPAL">PayPal</option>
        <option value="BANK_TRANSFER">무통장입금</option>
      </select>

      <div class="price-info">
        <p>{{ product.price.toLocaleString() }}원 / 박</p>
        <p>정가: {{ product.price.toLocaleString() }}원</p>
        <p v-if="product.discountedPrice !== product.price" class="text-red">
        할인 가격: {{ product.discountedPrice.toLocaleString() }}원
        </p>
      </div>

      <button @click="reserve">예약하기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const productId = route.params.id

const checkIn = ref(route.query.checkIn || new Date().toISOString().slice(0, 10))
const checkOut = ref(route.query.checkOut || '')
const adults = ref(route.query.adults || 2)
const paymentMethod = ref('')

const product = ref({
  title: '',
  address: '',
  description: '',
  price: 0,
  startDate: '',
  endDate: '',
  sellerId: null,
  categoryId: null,
  discount: null,
  discountedPrice: 0
})

// 이미지 URL도 상품 리스트랑 동일하게 경로 설정
const imageUrl = computed(() => `/api/files/image/product/${productId}`)

const userId = 1 // 임시 하드코딩 (로그인 정보로 교체 필요)

const totalNights = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0
  const inDate = new Date(checkIn.value)
  const outDate = new Date(checkOut.value)
  const diff = (outDate - inDate) / (1000 * 60 * 60 * 24)
  return diff > 0 ? diff : 0
})

const totalAmount = computed(() => {
  return product.value.price * totalNights.value * adults.value
})

const fetchProduct = async () => {
  try {
    const res = await axios.get(`/api/product/${productId}`)
    product.value = res.data
      console.log(product.value)
  } catch (e) {
    console.error('상품 상세 로딩 실패:', e)
  }
}

// 날짜 문자열 포맷 함수 (예: 2025-07-03T12:00:00 -> 2025-07-03 12:00)
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  if (isNaN(d)) return dateStr // 변환 불가시 원본 반환
  return d.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const reserve = async () => {
  if (!checkIn.value || !checkOut.value || !paymentMethod.value) {
    alert('체크인, 체크아웃 날짜와 결제 방법을 모두 선택해주세요.')
    return
  }
  if (totalNights.value <= 0) {
    alert('체크아웃 날짜는 체크인 날짜보다 늦어야 합니다.')
    return
  }

  try {
    const reservationData = {
      productId: Number(productId),
      userId,
      amount: totalAmount.value,
      paymentDate: new Date().toISOString(),
      paymentMethod: paymentMethod.value,
      refundDate: null,
      status: '예약대기',
      transactionId: null,
      checkIn: checkIn.value,
      checkOut: checkOut.value,
      adults: adults.value
    }

    console.log('예약 요청 데이터:', reservationData)

    await axios.post('/api/reservations', reservationData)

    alert('예약이 완료되었습니다.')
  } catch (error) {
    console.error('예약 실패:', error)
    alert('예약 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.product-detail {
  display: flex;
  flex-direction: column;
  max-width: 960px;
  margin: auto;
  padding: 24px;
}
.image-section img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-radius: 8px;
}
.info-section {
  margin-top: 24px;
  line-height: 1.6;
}
.booking-box {
  margin-top: 24px;
  padding: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
}
.booking-box label {
  display: block;
  margin-top: 8px;
  font-weight: bold;
}
.booking-box input,
.booking-box select {
  width: 100%;
  padding: 8px;
  margin-top: 4px;
  box-sizing: border-box;
}
.price-info {
  margin-top: 16px;
}
button {
  margin-top: 16px;
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
button:hover {
  background-color: #45a049;
}
.description {
  margin-top: 8px;
}
</style>
