<template>
    <div class="register-info">
    <div class="stepper">
      <div :class="['step', { active: currentStep === 1 }]">1. 약관 동의</div>
      <div :class="['step', { active: currentStep === 2 }]">2. 회원 정보 입력</div>
      <div :class="['step', { active: currentStep === 3 }]">3. 완료</div>
    </div>
  
  <div class="register-complete">
    <h2>회원가입 완료</h2>
    <p class="message">
      <strong>{{ userEmail }}</strong>로 인증 메일을 보냈습니다.<br />
      이메일을 확인하시고 인증을 완료하셔야<br />
      로그인하실 수 있습니다.
    </p>

    <div class="btn-group">
      <router-link to="/" class="main-btn">메인으로</router-link>
      <router-link to="/loginForm" class="login-btn">로그인</router-link>
    </div>

    <div class="email-help">
      <span>인증 메일을 받지 못했거나 문제가 발생했나요? </span>
      <button class="reissue-btn" @click="goReissue">재발급 요청</button>
    </div>
  </div>
  
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()

const currentStep = 3

const userEmail = route.query.email || 'unknown@example.com';
const userId = route.query.userId || null

async function goReissue() {
  try {
    console.log(userEmail)
    console.log(route.query.email)
    await axios.post('/api/email/reissue', {
      userId: userId,
      email: userEmail
    });
    alert('인증 메일이 재발송되었습니다.');
  } catch (error) {
    console.error(error);
    alert('메일 재발급에 실패했습니다.');
  }
}



</script>

<style scoped>
.register-info {
  max-width: 520px;
  margin: 48px auto;
  padding: 32px;
  background-color: #ffffff;
  border: 1px solid #e0e4ec;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  font-family: 'Noto Sans KR', sans-serif;
}

.stepper {
  display: flex;
  justify-content: space-between;
  margin-bottom: 36px;
}

.step {
  flex: 1;
  text-align: center;
  font-weight: 600;
  color: #c0c0c0;
  padding-bottom: 10px;
  border-bottom: 3px solid #e0e0e0;
  font-size: 15px;
  transition: color 0.3s, border-color 0.3s;
}

.step.active {
  color: #292e4c;
  border-color: #292e4c;
}

.register-complete {
  max-width: 480px;
  margin: 60px auto;
  padding: 36px 32px;
  font-family: 'Noto Sans KR', sans-serif;
  text-align: center;
}

h2 {
  color: #292e4c;
  font-weight: 800;
  font-size: 28px;
  margin-bottom: 28px;
  letter-spacing: 1px;
}

.message {
  font-size: 17px;
  color: #4a4a4a;
  line-height: 1.6;
  margin-bottom: 32px;
  user-select: text;
  border-top: 1px solid #e0e4ec;
  padding-top: 40px;
  padding-bottom: 20px;
}

.message strong {
  color: #1e2340;
  word-break: break-word;
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;
}

.main-btn,
.login-btn {
  flex: 1;
  min-width: 120px;
  padding: 14px 0;
  font-weight: 700;
  font-size: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 3px 6px rgba(41, 46, 76, 0.15);
  user-select: none;
  text-decoration: none;
  text-align: center;
}

.main-btn {
  background-color: #f2f4f8;
  border: 2px solid #292e4c;
  color: #292e4c;
}

.main-btn:hover {
  background-color: #292e4c;
  color: #ffffff;
  box-shadow: 0 5px 12px rgba(41, 46, 76, 0.4);
}

.login-btn {
  background-color: #292e4c;
  border: none;
  color: white;
}

.login-btn:hover {
  background-color: #1d2138;
  box-shadow: 0 5px 12px rgba(41, 46, 76, 0.4);
}

.email-help {
  font-size: 14.5px;
  color: #666;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  user-select: none;
  margin-bottom: 0;
}

.reissue-btn {
  background: none;
  border: none;
  color: #292e4c;
  font-weight: 700;
  cursor: pointer;
  text-decoration: underline;
  padding: 0;
  font-size: 14.5px;
  transition: color 0.3s ease;
}

.reissue-btn:hover {
  color: #1d2138;
}
</style>
