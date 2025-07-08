<template>
  <div class="reservation-detail" v-if="reservation && product && user">
    <h2>예약 상세 정보</h2>

    <div class="product-section">
      <img :src="imageUrl" alt="상품 이미지" />
      <div class="product-info">
        <h3>{{ product.title }}</h3>
        <p class="description">{{ product.description }}</p>
        <p><strong>숙소 주소 : </strong> {{ product.address }}</p>
        <p><strong>가격 : </strong> {{ product.price?.toLocaleString() }}원 / 박</p>
        <p><strong>결제일시 : </strong> {{ formatDate(reservation.paymentDate) }}</p>
      </div>
    </div>

    <div class="reservation-info">
      <p><strong>예약자 ID : </strong> {{ user.name }}</p>
      <p><strong>체크인 날짜 : </strong> {{ reservation.checkIn }}</p>
      <p><strong>체크아웃 날짜 : </strong> {{ reservation.checkOut }}</p>
      <p><strong>예약 인원 : </strong> {{ reservation.adults }}명</p>
      <p v-if="reservation.amount != null"><strong>총 결제 금액:</strong> {{ reservation.amount.toLocaleString() }}원</p>
      <p><strong>결제 방식 : </strong> {{ reservation.paymentMethod }}</p>
      <p style="display: flex; align-items: center;">
        <strong>예약 상태 : </strong>
        <span :class="statusClass" class="status-text">{{ displayStatus }}</span>

        <div class="status-buttons">
          <button class="btn refund" @click="handleRefundRequest">환불 요청</button>
          <button class="btn inquiry" @click="startChat">문의하기</button>
        </div>
      </p>
    </div>

    <div class="seller-info">
      <h3>판매자 정보</h3>
      <hr />

      <div class="seller-profile">
        <img
          :src="`/api/files/image/user/${reservation.sellerUserId}`"
          alt="판매자 프로필"
          class="seller-profile-img"
        />
        <div class="seller-details">
          <p><strong>이름 : </strong> {{ product.sellerNickname || '-' }}</p>
          <p><strong>연락처 : </strong> {{ product.sellerPhone || '-' }}</p>
          <p><strong>이메일 : </strong> {{ product.sellerEmail || '-' }}</p>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="loading">
    <p>예약 정보를 불러오는 중입니다...</p>
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
const reservationId = route.params.id


const isChatOpen = ref(false)
const selectedRoomInfo = ref(null)


const reservation = ref(null)
const product = ref(null)
const user = ref(null)

const imageUrl = computed(() =>
  product.value ? `/api/files/image/product/${product.value.id}` : ''
)

async function startChat() {
  try {
    const accessToken = localStorage.getItem('accessToken')
    const sellerId = product.value.sellerId 

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

// 상태에 따라 색상 클래스 지정
const statusClass = computed(() => {
  if (!reservation.value) return ''
  switch(reservation.value.status) {
    case '예약완료': return 'status-complete'
    case '취소됨': return 'status-cancel'
    case '환불중': return 'status-refund'
    default: return ''
  }
})

const displayStatus = computed(() => {
  if (!reservation.value || !reservation.value.status) return '-'

  switch(reservation.value.status) {
    case 'APPROVED':
      return '예약완료'
    case 'REFUNDED':
      return '환불완료'
    case 'CANCELED':
      return '취소됨'
    default:
      return reservation.value.status // 알 수 없는 상태면 원래 상태 그대로 보여주기
  }
})

const fetchReservation = async () => {
 try {
    const res = await axios.get(`/api/mypage/reservation/${reservationId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    reservation.value = res.data
    console.log(reservation.value)

    const productRes = await axios.get(`/api/product/${reservation.value.productId}`)
    product.value = productRes.data
    

    const userRes = await axios.get('/api/mypage/profile', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    user.value = userRes.data

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

async function handleRefundRequest() {
  try {
    confirm('결제를 취소하시겠습니까?')

    // const accessToken = localStorage.getItem('accessToken')
    
    // const refundRes = await axios.post(
    //   '/api/payments/refund',
    //   {
    //     tid: reservation.value.tid,
    //     cancelAmount: reservation.value.totalPrice
    //   },
    //   {
    //     headers: {
    //       'Content-Type': 'application/json'
    //     }
    //   }
    // )

    // 환불 성공
    // console.log('환불 성공:', refundRes.data)
    

    const refundRes = await axios.post(
      '/api/payments/refund/test',
      {
        tid: reservation.value.tid,
        cancelAmount: reservation.value.totalPrice
      },
    )

    alert('환불 요청이 완료되었습니다.')

    //환불 상태 UI 반영
    reservation.value.status = '환불완료'

  } catch (err) {
    console.error('환불 실패:', err)
    alert('환불에 실패했습니다: ' + (err.response?.data || err.message))
  }
}

onMounted(() => {
  fetchReservation()
})
</script>

<style scoped>
.reservation-detail {
  max-width: 900px;
  margin: 2rem auto;
  padding: 2rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 0 14px rgb(0 0 0 / 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
  font-weight: 700;
}

.product-section {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  align-items: flex-start;
}

.product-section img {
  width: 260px;
  height: 240px;
  border-radius: 10px;
  object-fit: cover;
  box-shadow: 0 4px 8px rgb(0 0 0 / 0.1);
  transition: transform 0.3s ease;
}
.product-section img:hover {
  transform: scale(1.05);
}

.product-info {
  flex: 1;
  font-size: 1rem;
  line-height: 1.5;
}
.product-info h3 {
  margin-bottom: 0.5rem;
  font-weight: 700;
  color: #34495e;
}
.product-info .description {
  margin-bottom: 1rem;
  color: #666;
}

.reservation-info {
  background-color: #fefefe;
  padding: 1.8rem 2rem;
  border-radius: 10px;
  border: 1px solid #d9d9d9;
  box-shadow: inset 0 0 8px rgb(0 0 0 / 0.02);
  font-size: 1rem;
  margin-bottom: 2rem;
}
.reservation-info p {
  margin: 0.4rem 0;
}
.reservation-info strong {
  color: #2c3e50;
}

.status-text {
  margin-left: 7px;
  font-weight: 700;
  min-width: 60px;
}

.status-complete {
  color: #27ae60;
  font-weight: 700;
}
.status-cancel {
  color: #e74c3c;
  font-weight: 700;
}
.status-refund {
  color: #f39c12;
  font-weight: 700;
}

.action-buttons {
  margin-top: 1.8rem;
  display: flex;
  gap: 1rem;
}
.btn {
  flex: none;              /* 버튼이 고정 크기 유지하도록 */
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

.btn:hover {
  background-color: #eee;  /* 마우스 올리면 연한 회색 배경 */
  color: #222;
  border-color: #555;
}

.btn.refund {
  border-color: #888;
  color: #444;
}

.btn.inquiry {
  border-color: #888;
  color: #444;
}

.seller-info {
  background: #f9f9f9;
  border-radius: 10px;
  padding: 1.8rem 2rem;
  box-shadow: 0 0 6px rgb(0 0 0 / 0.05);
  font-size: 1rem;
}
.seller-info h3 {
  margin-bottom: 1rem;
  color: #34495e;
}
.seller-info p {
  margin: 0.3rem 0;
  color: #555;
}

.loading {
  max-width: 900px;
  margin: 3rem auto;
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

.action-buttons {
  margin-top: 1.5rem;
  display: flex;
  gap: 0.8rem;
}

.reservation-info p button.btn {
  flex: none;
  padding: 0.35rem 0.8rem;
  font-size: 0.85rem;
  border-radius: 6px;
  border: 1.5px solid #888;
  background: transparent;
  color: #444;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.reservation-info p button.btn:hover {
  background-color: #eee;
  color: #222;
  border-color: #555;
}

.status-buttons {
  margin-left: auto; /* 버튼 그룹을 오른쪽 끝으로 밀기 */
  display: flex;
  gap: 0.8rem;
}

.status-buttons .btn {
  flex: none;
  padding: 0.35rem 0.8rem;
  font-size: 0.85rem;
  border-radius: 6px;
  border: 1.5px solid #888;
  background: transparent;
  color: #444;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.status-buttons .btn:hover {
  background-color: #eee;
  color: #222;
  border-color: #555;
}

.seller-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.seller-profile-img {
  width: 80px;
  height: 80px;
  border-radius: 50%; 
  object-fit: cover;
  border: 1px solid #ccc; 
}

</style>
