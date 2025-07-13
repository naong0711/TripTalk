<template>
  <p>로그인 처리 중입니다...</p>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

onMounted(() => {
  const accessToken = route.query.accessToken
  const refreshToken = route.query.refreshToken

  if (accessToken && refreshToken) {
    // 예: localStorage에 저장
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)

    // 홈으로 이동
    router.push('/')
  } else {
    alert('로그인 실패: 토큰 없음')
    router.push('/login')
  }
})
</script>
