<template>
  <div class="profile-detail">
    <h2>회원 상세 정보</h2>
    <div class="info-row" v-for="(value, key) in userInfo" :key="key">
      <strong>{{ labels[key] }}:</strong>
      <span>{{ value || '정보 없음' }}</span>
    </div>
    <button @click="goBack">돌아가기</button>    
    <button @click="showPasswordModal = true">수정하기</button>
    
    <PasswordConfirmModal
    v-if="showPasswordModal"
    @confirm="handlePasswordConfirm"
    @cancel="showPasswordModal = false"
    />
    
</div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import PasswordConfirmModal from './PasswordConfirmModal.vue'

const router = useRouter()

const userInfo = ref({})

const showPasswordModal = ref(false)
function handlePasswordConfirm() {
  showPasswordModal.value = false
  router.push('/mypage/update')
}
const labels = {
  userId: '아이디',
  name: '이름',
  email: '이메일',
  nickname: '닉네임',
  phone: '전화번호',
  birthDate: '생년월일',
  zipcode: '우편번호',
  address: '주소',
  addressDetail: '상세주소',
  loginType: '로그인 유형',
  emailVerified: '이메일 인증 여부',
  role: '권한'
}

onMounted(async () => {
  try {
    const res = await axios.get('/api/mypage/profile', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    userInfo.value = res.data
  } catch (error) {
    console.error('회원 정보 조회 실패:', error)
  }
})

function goBack() {
  router.back()
}
</script>

<style scoped>
.profile-detail {
  max-width: 500px;
  margin: 40px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 12px;
  background: #fefefe;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  font-family: 'Noto Sans KR', sans-serif;
}
h2 {
  text-align: center;
  margin-bottom: 24px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
.info-row strong {
  color: #666;
  width: 130px;
}
.info-row span {
  color: #333;
  flex: 1;
  text-align: right;
}
button {
  margin-top: 24px;
  display: block;
  width: 100%;
  background-color: #c8ad7f;
  color: white;
  border: none;
  padding: 12px;
  font-size: 16px;
  border-radius: 6px;
  cursor: pointer;
}
button:hover {
  background-color: #b4976f;
}
</style>
