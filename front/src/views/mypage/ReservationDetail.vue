<template>
  <div class="reservation-detail" v-if="reservation && product">
    <h2>예약 상세 정보</h2>

    <div class="product-section">
      <img :src="imageUrl" alt="상품 이미지" />
      <div class="product-info">
        <h3>{{ product.title }}</h3>
        <p>{{ product.description }}</p>
        <p><strong>주소:</strong> {{ product.address }}</p>
        <p><strong>가격:</strong> {{ product.price.toLocaleString() }}원 / 박</p>
        <p><strong>예약일:</strong> {{ formatDate(reservation.paymentDate) }}</p>
      </div>
    </div>

    <div class="reservation-info">
      <p><strong>예약자 ID:</strong> {{ reservation.userId }}</p>
      <p><strong>체크인:</strong> {{ reservation.checkIn }}</p>
      <p><strong>체크아웃:</strong> {{ reservation.checkOut }}</p>
      <p><strong>인원 수:</strong> {{ reservation.adults }}명</p>
      <p><strong>결제 금액:</strong> {{ reservation.amount.toLocaleString() }}원</p>
      <p><strong>결제 수단:</strong> {{ reservation.paymentMethod }}</p>
      <p><strong>상태:</strong> {{ reservation.status }}</p>
    </div>
  </div>

  <div v-else>
    <p>예약 정보를 불러오는 중입니다...</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const reservationId = route.params.id

const reservation = ref(null)
const product = ref(null)

const imageUrl = computed(() =>
  product.value ? `/api/files/image/product/${product.value.id}` : ''
)

const fetchReservation = async () => {
 try {
    const res = await axios.get(`/api/mypage/reservation/${reservationId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    reservation.value = res.data

    // 예약된 상품 정보도 같이 가져오기
    const productRes = await axios.get(`/api/product/${reservation.value.productId}`)
    product.value = productRes.data
  } catch (err) {
    console.error('예약 상세 정보 로딩 실패:', err)
    alert('예약 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchReservation()
})
</script>

<style scoped>
.reservation-detail {
  max-width: 960px;
  margin: auto;
  padding: 24px;
}

.product-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.product-section img {
  width: 240px;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
}

.product-info {
  flex: 1;
  line-height: 1.6;
}

.reservation-info {
  padding: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  line-height: 1.6;
}
</style>
