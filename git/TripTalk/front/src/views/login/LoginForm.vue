<template>
  <div class="login-form">
    <h2>로그인</h2>
    <form @submit.prevent="onSubmit">
      <div class="form-group">
        <label for="username">아이디</label>
        <input
          id="userid"
          v-model="userid"
          type="text"
          placeholder="아이디를 입력하세요"
          required
          autocomplete="userid"
        />
      </div>

      <div class="form-group">
        <label for="password">비밀번호</label>
        <input
          id="password"
          v-model="password"
          type="password"
          placeholder="비밀번호를 입력하세요"
          required
          autocomplete="current-password"
        />
      </div>

      <div class="remember-me">
        <label class="custom-checkbox">
          <input type="checkbox" id="remember" />
          <span class="checkmark"></span>
          <span>아이디 저장</span>
        </label>
      </div>

      <button type="submit" class="login-btn">로그인</button>
    </form>

    <div class="helper-links">
      <button class="modal-open-btn" @click="showFindIdPwModal = true">아이디/비밀번호 찾기</button>
      <span>|</span>
      <router-link to="/register/agree">회원가입</router-link>
    </div>

    <div class="divider">
    <hr />
        <span> OR </span>
        <hr />
    </div>

    <div class="social-login">
        <img 
          src="@/assets/loginBtn/kakao_login.png" 
          alt="카카오 로그인" 
          class="social-icon" 
          style="cursor:pointer;"
          @click="handleKakaoLogin" 
        />
        <img src="@/assets/loginBtn/naver_login.png" alt="네이버 로그인" class="social-icon" />
        <img src="@/assets/loginBtn/google_login.png" alt="구글 로그인" class="social-icon" />
    </div>

       <FindIdPwModal v-if="showFindIdPwModal" @close="showFindIdPwModal = false" />
  </div>
      <NewPasswordModal
        v-if="showChangePasswordModal"
        @success="handlePasswordChangeSuccess"
        @cancel="showChangePasswordModal = false"
      />

</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import FindIdPwModal from '@/components/FindIdPw.vue'
import NewPasswordModal from '@/components/NewPasswordModal.vue'

const userid = ref('')
const password = ref('')
const showFindIdPwModal = ref(false) 
const showChangePasswordModal = ref(false)
const router = useRouter()

function handlePasswordChangeSuccess() {
  alert('완료되었습니다.')
  showChangePasswordModal.value = false
  router.push('/')
}

async function onSubmit() {
  console.log('userid:', userid.value)
  console.log('password:', password.value)

  try {
    const response = await axios.post('/api/user/login', {
      userId: userid.value,
      password: password.value,
    })

    console.log('로그인 성공:', response.data)
    
    //응답 토큰 저장
    localStorage.setItem('accessToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);

    console.log(response.data.tempPasswordUsed)

    const userId = getUserIdFromToken(response.data.accessToken);
    if (userId) {
      localStorage.setItem('userId', userId);
      console.log(userId);
    } else {
      console.warn('userId 추출 실패: JWT 형식 확인 필요');
    }

    if (response.data.tempPasswordUsed) {
  showChangePasswordModal.value = true;
  } else {
    router.push('/')
  }

  } catch (error) {
    console.error('로그인 실패:', error.response || error.message)
    alert('로그인 실패: 아이디 또는 비밀번호를 확인하세요.')
  }
}

function goToChangePw() {
  showChangePasswordModal.value = false
  router.push('/mypage/change-password')
}

function getUserIdFromToken(token) {
  try {
    const payloadBase64 = token.split('.')[1];
    const payload = JSON.parse(atob(payloadBase64));
    return payload.id;
  } catch (e) {
    console.error('Invalid token');
    return null;
  }
}

function handleKakaoLogin() {
  window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
}

</script>


<style scoped>
.login-form {
  max-width: 360px;
  margin: 48px auto;
  padding: 32px;
  background-color: #ffffff;
  border: 1px solid #e0e4ec;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  font-family: 'Noto Sans KR', sans-serif;
}

h2 {
  text-align: center;
  font-size: 24px;
  color: #292e4c;
  font-weight: 800;
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #2c2c2c;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #bbb;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.2s ease;
}

input[type="text"]:focus,
input[type="password"]:focus {
  outline: none;
  border-color: #292e4c;
}

.login-btn {
  width: 100%;
  padding: 14px 0;
  background-color: #292e4c;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.login-btn:hover {
  background-color: #1d2138;
}

.remember-me {
  margin-bottom: 18px;
}

.remember-me label {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  color: #333;
  cursor: pointer;
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin-right: 6px;
  vertical-align: middle;
}

.helper-links {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin: 14px 0;
  font-size: 13px;
}

.helper-links a,
.helper-links .modal-open-btn {
  color: #292e4c;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  font-weight: 600;
  text-decoration: underline;
}

.helper-links a:hover,
.helper-links .modal-open-btn:hover {
  color: #1d2138;
  text-decoration: underline;
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 30px 0 16px;
}

.divider hr {
  flex: 1;
  border: none;
  border-top: 1px solid #ccc;
}

.divider span {
  padding: 0 12px;
  color: #666;
  font-weight: 600;
  font-size: 13px;
}

.social-login {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.social-icon {
  width: 40px;
  cursor: pointer;
  padding: 0 5px;
  object-fit: contain;
}

.custom-checkbox {
  position: relative;
  display: inline-flex;
  align-items: center;
  padding-left: 28px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  user-select: none;
}

.custom-checkbox input[type="checkbox"] {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.custom-checkbox .checkmark {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  height: 18px;
  width: 18px;
  background-color: #fff;
  border: 2px solid #6b7280;
  border-radius: 4px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.custom-checkbox input[type="checkbox"]:checked ~ .checkmark {
  background-color: #292e4c;
  border-color: #292e4c;
}

.custom-checkbox .checkmark::after {
  content: "";
  position: absolute;
  display: none;
  left: 4px;
  bottom: 3px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.custom-checkbox input[type="checkbox"]:checked ~ .checkmark::after {
  display: block;
}
</style>
