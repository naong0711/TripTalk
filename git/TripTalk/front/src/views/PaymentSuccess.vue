<!-- src/views/PaymentSuccess.vue -->
<template>
  <div class="payment-success">
    <h2>🎉 예약이 완료되었습니다!</h2>
    <p>결제가 성공적으로 처리되었으며, 예약이 등록되었습니다.</p>
    <p>잠시 후 메인 페이지로 이동합니다...</p>
    <router-link to="/">👉 홈으로 가기</router-link>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // 5초 후 홈으로 이동
  setTimeout(() => {
    router.push('/')
  }, 5000)
})
</script>

<style scoped>
.payment-success {
  max-width: 600px;
  margin: 80px auto;
  text-align: center;
  font-family: 'Noto Sans KR', sans-serif;
}
.payment-success h2 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #2a9d8f;
}
</style>