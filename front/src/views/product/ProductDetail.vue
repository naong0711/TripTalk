<template>
  <div class="product-detail">

    <div class="left-menu">
      <!-- 1. 이미지 -->
      <div class="image-section">
        <img :src="imageUrl" alt="상품 이미지" />
      </div>

      <!-- 2. 상품 설명 (이미지 아래로 내려옴) -->
      <div class="description-box">
        <h2>{{ product.title }}</h2>
        <p class="description">{{ product.description }}</p>
        <p><strong>주소:</strong> {{ product.address }}</p>
        <p><strong>지역:</strong> {{ product.location }}</p>
        <p><strong>시작일:</strong> {{ formatDate(product.startDate) }}</p>
        <p><strong>종료일:</strong> {{ formatDate(product.endDate) }}</p>
        <p><strong>인원수:</strong> {{ product.minPeople }}명 ~ {{ product.maxPeople }}명</p>

        <div v-if="product.discount" class="discount-info">
          <h3>할인 정보</h3>
          <p><strong>{{ product.discount.name }} : </strong> {{ (product.discount.discountRate * 100).toFixed(1) }}%</p>
          <p><strong>할인 기간 : </strong> {{ formatDate(product.discount.startAt) }} ~  {{ formatDate(product.discount.endAt) }}</p>
        </div>
        <div v-else="product.discount" class="discount-info">
          <h3>할인 정보</h3>
          <p><strong>-</strong></p>
        </div>
      </div>


    </div>

    <!-- 3. 예약하기 (기존 info-section 자리에 위치) -->
    <div class="booking-box">
      <h3>예약하기</h3>

      <div class="price-wrap">
        <p class="price">
          <span class="label">가격</span>
          <span class="amount">{{ product.price.toLocaleString() }}원 / 박</span>
        </p>
        <p v-if="product.discount && product.discountedPrice !== product.price" class="discounted-price">
          할인 가격: {{ product.discountedPrice.toLocaleString() }}원
        </p>
      </div>

      <label for="checkin">체크인</label>
      <input id="checkin" type="date" v-model="checkIn" :min="today" />

      <label for="checkout">체크아웃</label>
      <input id="checkout" type="date" v-model="checkOut" :min="checkIn" />

      <label for="adults">인원</label>
      <input id="adults" type="number" min="1" v-model.number="adults" placeholder="인원 수" :max="product.maxPeople" />

      <label for="paymentMethod">결제 방법</label>
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

    <!-- 4. 판매자 정보 (기존 예약하기 박스 자리) -->
    <div class="seller-box">
      <h3>판매자 정보</h3>
      <div class="seller-profile">
       <img
          :src="sellerProfileImage"
          alt="판매자 프로필"
          class="seller-profile-img"
        />
        <div class="seller-details">
          <p><strong>이름 :</strong> {{ product.sellerNickname || '-' }}</p>
          <p><strong>연락처 :</strong> {{ product.sellerPhone || '-' }}</p>
          <p><strong>이메일 :</strong> {{ product.sellerEmail || '-' }}</p>
        </div>
        <button class="btn inquiry" @click="startChat">문의하기</button>
      </div>

    </div>
  </div>

  <ChatModal
      v-if="isChatOpen"
      :selectedRoom="selectedRoomInfo"
      @close="isChatOpen = false"
    />
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import ChatModal from '@/components/chat/ChatModal.vue'

const route = useRoute()
const productId = route.params.id

const defaultProfileImage = new URL('@/assets/default-profile.png', import.meta.url).href
const sellerProfileImage = computed(() => {
  console.log(product.value.sellerUserId)
  if (!product.value || !product.value.sellerUserId) {
    return defaultProfileImage
  }
  return `/api/files/image/user/${product.value.sellerUserId}`
})


const today = new Date().toISOString().slice(0, 10)

const checkIn = ref(route.query.checkIn || today)
const checkOut = ref(route.query.checkOut || '')
const adults = ref(route.query.adults || 2)
const paymentMethod = ref('')
const isChatOpen = ref(false)
const selectedRoomInfo = ref(null)

const product = ref({
  title: '',
  address: '',
  description: '',
  price: 0,
  startDate: '',
  endDate: '',
  sellerId: null,
  sellerUserId: null, // 프로필 이미지용
  sellerNickname: '',
  sellerPhone: '',
  sellerEmail: '',
  categoryId: null,
  discount: null,
  discountedPrice: 0,
  minPeople: 1,
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

async function startChat() {
  try {
    const accessToken = localStorage.getItem('accessToken')
    const sellerId = product.value.sellerId 

    if(!accessToken) {
      alert('로그인 후 이용해주세요.')
      return
    }

    const res = await axios.get(`/api/chat/room/${sellerId}`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    });

    const roomId = res.data

    selectedRoomInfo.value = {
      roomId,
      receiverId: sellerId,
      userRole: 'BUYER',
      opponentName: product.value.sellerNickname || '판매자'
    }

    isChatOpen.value = true

  } catch (err) {
    console.error('채팅방 ID 조회 실패:', err)
    alert('채팅방을 여는 데 실패했습니다.')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  if (isNaN(d)) return dateStr
  const year = d.getFullYear()
  const month = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  return `${year}. ${month}. ${day}`
}

const reserve = async () => {

  
  const token = localStorage.getItem('accessToken')

  if(!token) {
    alert('로그인 후 이용해주세요.')
    return
  }

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

.description-box {
  flex: 1 1 100%;
  background: #fff;
  padding: 24px;
  margin-top: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  line-height: 1.6;
}

.description-box h2 {
  font-size: 26px;
  font-weight: 700;
  color: #222;
  margin-bottom: 12px;
}

.description-box p {
  margin: 8px 0;
  color: #555;
  font-size: 16px;
}

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
  gap: 15px;
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
  margin-bottom: 5px;
  font-size: 22px;
  font-weight: 700;
  color: #292e4c;
  padding-bottom: 10px;
  border-bottom: #eee solid 1px;
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
  border-color: #4c589b;
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
  background-color: #292e4c;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

button:hover {
  background-color: #1d2138;
}

.seller-box {
  flex: 1 1 320px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  font-size: 1rem;
}

.seller-box h3 {
  margin-bottom: 16px;
  font-size: 22px;
  font-weight: 700;
  color: #222;
  text-align: center;
}

.seller-profile {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-top: 1rem;
}

.seller-profile-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid #ccc;
}

.seller-details p {
  margin: 0.3rem 0;
  color: #444;
  font-size: 15px;
}

.btn.inquiry {
  flex: none;              /* 버튼이 고정 크기 유지하도록 */
  margin-left: 480px;
  padding: 0.4rem 1rem;    /* 작고 여유 있는 패딩 */
  font-weight: 600;
  font-size: 0.9rem;
  border-radius: 6px;
  cursor: pointer;
  border: 1.5px solid #888; /* 연한 회색 테두리 */
  background-color: transparent;
  color: #444;
  user-select: none;
  transition: background-color 0.2s ease, color 0.2s ease;
}

</style>