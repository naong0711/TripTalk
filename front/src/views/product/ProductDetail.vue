<template>
  <div class="product-detail">
    <div class="image-section">
      <img :src="imageUrl" alt="상품 이미지" />
    </div>

    <div class="info-section">
      <h2>{{ product.title }}</h2>
      <p class="description">{{ product.description }}</p>
      <p><strong>주소:</strong> {{ product.address }}</p>
      <p><strong>지역:</strong> {{ product.location }}</p>
      <div class="price-wrap">
        <p class="price">
          <span class="label">가격</span>
          <span class="amount">{{ product.price.toLocaleString() }}원 / 박</span>
        </p>
        <p v-if="product.discount && product.discountedPrice !== product.price" class="discounted-price">
          할인 가격: {{ product.discountedPrice.toLocaleString() }}원
        </p>
      </div>

      <p><strong>시작일:</strong> {{ formatDate(product.startDate) }}</p>
      <p><strong>종료일:</strong> {{ formatDate(product.endDate) }}</p>
      <p><strong>인원수:</strong> {{ product.minPeople }}명 ~ {{ product.maxPeople }}명</p>
      <div v-if="product.discount" class="discount-info">
        <h3>할인 정보</h3>
        <p><strong>할인 이름:</strong> {{ product.discount.name }}</p>
        <p><strong>할인율:</strong> {{ (product.discount.discountRate * 100).toFixed(1) }}%</p>
        <p><strong>할인 시작일:</strong> {{ formatDate(product.discount.startAt) }}</p>
        <p><strong>할인 종료일:</strong> {{ formatDate(product.discount.endAt) }}</p>
      </div>
    </div>

    <div class="booking-box">
      <h3>예약하기</h3>

      <label for="checkin">체크인</label>
      <input id="checkin" type="date" v-model="checkIn" :min="today" />

      <label for="checkout">체크아웃</label>
      <input id="checkout" type="date" v-model="checkOut" :min="checkIn" />

      <label for="adults">인원</label>
      <input id="adults" type="number" min="1" v-model.number="adults" placeholder="인원 수" />

      <select id="paymentMethod" v-model="paymentMethod">
        <option disabled value="">-- 결제 방법 선택 --</option>
        <option value="CARD">신용카드</option>
        <option value="KAKAO_PAY">카카오페이</option>
        <option value="PAYPAL">PayPal</option>
        <option value="BANK_TRANSFER">무통장입금</option>
      </select>


      <div class="price-info">
        <p>총 숙박일: <strong>{{ totalNights }}</strong> 박</p>
        <p>총 금액: <strong>{{ totalAmount.toLocaleString() }}원</strong></p>
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

const today = new Date().toISOString().slice(0, 10)

const checkIn = ref(route.query.checkIn || today)
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
  discountedPrice: 0,
  minPeople: 1,    // 기본값 추가
  maxPeople: 1,
})

const imageUrl = computed(() => `/api/files/image/product/${productId}`)

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

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  if (isNaN(d)) return dateStr
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

    // 인원수 범위 체크
  if (adults.value < product.value.minPeople) {
    alert(`최소 인원수는 ${product.value.minPeople}명 입니다.`)
    return
  }
  if (adults.value > product.value.maxPeople) {
    alert(`최대 인원수는 ${product.value.maxPeople}명 입니다.`)
    return
  }

  try {
    const paymentRequest = {
      productId: Number(productId),
      amount: totalAmount.value,
      paymentMethod: paymentMethod.value,
      paymentId: '', // 백엔드에서 처리될 값
      status: ''     // 백엔드에서 처리될 값
    }

  const token = localStorage.getItem('accessToken')
   const response = await axios.post('/api/payments/create', paymentRequest,
  {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
  )
    const redirectUrl = response.data.next_redirect_pc_url
    if (redirectUrl) {
      window.location.href = redirectUrl
    } else {
      alert('카카오페이 결제 URL이 없습니다.')
    }
  } catch (error) {
    console.error('결제 준비 중 오류 발생:', error)
    alert('결제 준비 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>


<style scoped>
.product-detail {
  max-width: 960px;
  margin: 40px auto;
  padding: 24px;
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
  background-color: #fafafa;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
}

.image-section {
  flex: 1 1 400px;
  max-width: 480px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.image-section img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  display: block;
}

.info-section {
  flex: 1 1 400px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  line-height: 1.6;
}

.info-section h2 {
  margin-bottom: 12px;
  font-size: 28px;
  font-weight: 700;
  color: #222;
}

.description {
  font-size: 16px;
  margin-bottom: 16px;
  color: #555;
}

.price-wrap {
  margin: 16px 0;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #444;
  margin-bottom: 8px;
}

.price .label {
  color: #888;
  font-weight: 400;
  margin-right: 8px;
}

.discounted-price {
  font-size: 20px;
  font-weight: 700;
  color: #e63946; /* 진한 빨간색 */
  margin-top: 0;
}

.discount-info {
  margin-top: 24px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.discount-info h3 {
  font-weight: 700;
  color: #1d3557;
  margin-bottom: 12px;
}

.booking-box {
  flex: 1 1 320px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
}

.booking-box h3 {
  margin-bottom: 16px;
  font-size: 22px;
  font-weight: 700;
  color: #222;
  text-align: center;
}

.booking-box label {
  margin-top: 12px;
  font-weight: 600;
  color: #444;
}

.booking-box input,
.booking-box select {
  margin-top: 6px;
  padding: 10px 12px;
  font-size: 16px;
  border: 1.5px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.3s ease;
}

.booking-box input:focus,
.booking-box select:focus {
  border-color: #4caf50;
  outline: none;
}

.price-info {
  margin-top: 20px;
  font-weight: 700;
  font-size: 18px;
  color: #222;
  text-align: center;
}

button {
  margin-top: 28px;
  padding: 14px 20px;
  font-size: 18px;
  font-weight: 700;
  background-color: #2a9d8f;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

button:hover {
  background-color: #21867a;
}
</style>