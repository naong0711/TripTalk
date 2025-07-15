<template>
  <div class="profile-detail">
    <h2>회원 상세 정보</h2>

    <dl class="info-list">
      <template v-for="(value, key) in filteredUserInfo" :key="key">
        <dt>{{ labels[key] || key }}</dt>
        <dd :title="value || '정보 없음'">{{ value || '정보 없음' }}</dd>
      </template>
    </dl>

  <div class="btn-group">
    <button class="btn-back" @click="goBack">뒤로 가기</button>
    <button class="btn-edit" @click="showPasswordModal = true">정보 수정</button>
  </div>

    <PasswordConfirmModal
      v-if="showPasswordModal"
      @confirm="handlePasswordConfirm"
      @cancel="showPasswordModal = false"
    />
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import PasswordConfirmModal from '@/components/PasswordConfirmModal.vue'

const router = useRouter()
const userInfo = ref({})
const showPasswordModal = ref(false)

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
}

const filteredUserInfo = computed(() => {
  const excludeKeys = ['sellerId', 'profileImageUrl']
  return Object.fromEntries(
    Object.entries(userInfo.value).filter(([key]) => !excludeKeys.includes(key))
  )
})

function handlePasswordConfirm() {
  showPasswordModal.value = false
  router.push('/mypage/update')
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
  max-width: 480px;
  margin: 40px auto;
  padding: 32px 36px;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  font-family: 'Noto Sans KR', sans-serif;
  color: #444;
  user-select: none;
}

h2 {
  font-weight: 700;
  font-size: 30px;
  color: #3a3a3a;
  margin-bottom: 36px;
  text-align: center;
  letter-spacing: 0.02em;
}

/* 정보 리스트: dt는 label, dd는 값 */
.info-list {
  display: grid;
  grid-template-columns: 130px 1fr;
  row-gap: 18px;
  column-gap: 24px;
  margin-bottom: 48px;
}

.info-list dt {
  font-weight: 600;
  color: #292e4c;
  font-size: 16px;
  align-self: center;
  user-select: text;
}

.info-list dd {
  margin: 0;
  font-size: 16px;
  color: #222;
  text-align: left;
  user-select: text;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 버튼 그룹 */
.btn-group {
  display: flex;
  gap: 18px;
  justify-content: center;
}

.btn-back,
.btn-edit {
  flex: 1;
  padding: 14px 0;
  font-size: 18px;
  font-weight: 700;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.25s ease;
  user-select: none;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

/* 뒤로가기 버튼 */
.btn-back {
  background-color: #f2f1ee;
  color: #555;
}

.btn-back:hover {
  background-color: #d3cfc2;
  color: #222;
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}

/* 수정하기 버튼 */
.btn-edit {
  background-color: #292e4c;
  color: #fff;
}

.btn-edit:hover {
  background-color: #1d2138;
  box-shadow: 0 4px 10px rgba(168,141,77,0.6);
}

/* 반응형: 모바일 세로 정렬 */
@media (max-width: 400px) {
  .btn-group {
    flex-direction: column;
  }
}

</style>
