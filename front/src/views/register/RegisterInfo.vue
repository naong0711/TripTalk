<template>
  <div class="register-info">
    <div class="stepper">
      <div :class="['step', { active: currentStep === 1 }]">1. 약관 동의</div>
      <div :class="['step', { active: currentStep === 2 }]">2. 회원 정보 입력</div>
      <div :class="['step', { active: currentStep === 3 }]">3. 완료</div>
    </div>

    <h2>회원 정보 입력</h2>

    <form @submit.prevent="goNext">
      <!-- 계정 정보 -->
      <fieldset>
        <legend>계정 정보</legend>

        <div class="form-group">
          <label for="userId">아이디<span class="required">*</span></label>
          <div class="input-with-btn">
            <input id="userId" v-model.trim="form.userId" type="text" placeholder="아이디를 입력하세요" required />
            <button type="button" class="check-btn" @click="checkDuplicateId">중복검사</button>
          </div>
        </div>

        <div class="form-group">
          <label for="password">
            비밀번호<span class="required">*</span>
            <small class="hint"> (8자 이상, 영문/숫자/특수문자 포함)</small>
          </label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            required
          />
        </div>

        <div class="form-group">
          <label for="passwordConfirm">비밀번호 확인<span class="required">*</span></label>
          <input
            id="passwordConfirm"
            v-model="form.passwordConfirm"
            type="password"
            placeholder="비밀번호를 다시 입력하세요"
            required
          />
          <p v-if="form.password && form.passwordConfirm && form.password !== form.passwordConfirm" class="pw-msg">
            비밀번호가 일치하지 않습니다.
          </p>
          <!-- 비밀번호 유효성 검증 메시지 -->
          <p v-if="form.password && !isPasswordValid" class="pw-msg">
            비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다.
          </p>
        </div>
      </fieldset>

      <!-- 사용자 정보 -->
      <fieldset>
        <legend>사용자 정보</legend>

        <div class="form-group">
          <label for="name">이름<span class="required">*</span></label>
          <input id="name" v-model.trim="form.name" type="text" placeholder="이름을 입력하세요" required />
        </div>

        <div class="form-group">
          <label for="nickname">닉네임<span class="required">*</span></label>
          <input id="nickname" v-model.trim="form.nickname" type="text" placeholder="닉네임을 입력하세요" required />
        </div>

        <div class="form-group">
          <label>이메일<span class="required">*</span></label>
          <div class="email-group">
            <input v-model.trim="form.emailId" type="text" placeholder="이메일 아이디" required />
            <span class="at-symbol">@</span>
            <select v-model="form.emailDomain" class="email-select" required>
              <option value="">선택</option>
              <option value="gmail.com">gmail.com</option>
              <option value="naver.com">naver.com</option>
              <option value="daum.net">daum.net</option>
              <option value="kakao.com">kakao.com</option>
              <option value="direct">직접 입력</option>
            </select>
          </div>
          <div v-if="form.emailDomain === 'direct'" class="form-group" style="margin-top: 8px">
            <input v-model.trim="form.customEmailDomain" type="text" placeholder="도메인을 입력하세요 (예: example.com)" required />
          </div>
        </div>

        <div class="form-group">
          <label for="phone">전화번호<span class="required">*</span></label>
          <input id="phone" v-model.trim="form.phone" type="tel" placeholder="전화번호를 입력하세요" required />
        </div>

        <div class="form-group">
          <label for="birthdate">생년월일<span class="required">*</span></label>
          <input id="birthdate" v-model="form.birthDate" type="date" class="date-input styled-input" required />
        </div>

        <!-- 주소 -->
        <div class="form-group">
          <label>주소<span class="required">*</span></label>
          <div class="input-with-btn">
            <input v-model="form.zipcode" type="text" placeholder="우편번호" />
            <button type="button" class="check-btn" @click="searchAddress">주소 찾기</button>
          </div>
          <input v-model="form.address" type="text" placeholder="도로명주소"  style="margin-top: 8px" />
          <input v-model="form.addressDetail" type="text" placeholder="상세주소를 입력하세요" style="margin-top: 8px" />
        </div>
      </fieldset>

      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      <button type="submit" class="next-btn">다음으로</button>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentStep = 2

const form = reactive({
  userId: '',
  password: '',
  passwordConfirm: '',
  name: '',
  nickname: '',
  emailId: '',
  emailDomain: '',
  customEmailDomain: '',
  phone: '',
  birthDate: '',
  zipcode: '',
  address: '',
  addressDetail: '',
})

const errorMsg = ref('')
const isUserIdChecked = ref(false)
const isPasswordValid = ref(true)

watch(() => form.emailDomain, (newVal) => {
  if (newVal !== 'direct') form.customEmailDomain = ''
})

// 비밀번호 유효성 검사 함수
function validatePassword(password) {
  const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&]).{8,}$/
  return regex.test(password)
}

async function checkDuplicateId() {
  if (!form.userId) {
    alert('아이디를 입력해주세요.')
    return
  }

  try {
    const response = await axios.get('/api/user/checkId', {
      params: { userId: form.userId }
    })

    if (response.data) {
      alert('이미 사용 중인 아이디입니다.')
      isUserIdChecked.value = false
    } else {
      alert(`아이디 "${form.userId}"은 사용 가능합니다.`)
      isUserIdChecked.value = true
    }
  } catch (error) {
    alert('아이디 중복 확인 중 오류가 발생했습니다.')
    isUserIdChecked.value = false
  }
}

function searchAddress() {
  new window.daum.Postcode({
    oncomplete: function (data) {
      form.zipcode = data.zonecode;
      form.address = data.roadAddress || data.jibunAddress;
    },
  }).open();
}

function formatPhoneNumber(phone) {
  if (!phone) return '';
  // 하이픈 이미 있으면 그대로 리턴
  if (phone.includes('-')) return phone;
  if (phone.length === 11) return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
  if (phone.length === 10) return phone.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
  return phone; // 형식 안 맞으면 그대로
}

async function goNext() {
  errorMsg.value = ''

  const requiredFields = [
    form.userId, form.password, form.passwordConfirm,
    form.name, form.nickname,
    form.emailId,
    form.emailDomain === 'direct' ? form.customEmailDomain : form.emailDomain,
    form.phone, form.birthDate,
    form.zipcode, form.address
  ]

  if (requiredFields.some(v => !v)) {
    errorMsg.value = '모든 필수 항목을 입력해주세요.'
    return
  }

  if (!isUserIdChecked.value) {
    errorMsg.value = '아이디 중복 확인을 해주세요.'
    return
  }

  isPasswordValid.value = validatePassword(form.password)
  if (!isPasswordValid.value) {
    errorMsg.value = '비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다.'
    return
  }

  if (form.password !== form.passwordConfirm) {
    errorMsg.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  const fullEmail = `${form.emailId}@${form.emailDomain === 'direct' ? form.customEmailDomain : form.emailDomain}`
  const formattedPhone = formatPhoneNumber(form.phone);
  
  try {
    const requestData = {
      userId: form.userId,
      password: form.password,
      name: form.name,
      nickname: form.nickname,
      email: fullEmail,
      phone: formattedPhone,
      birthDate: form.birthDate,
      zipcode: form.zipcode,
      address: form.address,
      addressDetail: form.addressDetail || null

    }

    console.log(requestData)

    const response = await axios.post('/api/user/register', requestData)
    console.log(response)
    console.log(response.data)
    const email = response.data.email
    console.log(email)

    alert('회원가입 성공! 이메일을 확인해 인증을 완료해주세요.')
    router.push({
      path: '/register/complete',
      query: { email }  // 쿼리 파라미터로 email 전달
    });
    
  } catch (error) {
    console.error(error)
    errorMsg.value = '회원가입에 실패했습니다.'
  }
}
</script>

<style scoped>
.register-info {
  max-width: 480px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-family: 'Noto Sans KR', sans-serif;
}

.stepper {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.step {
  flex: 1;
  text-align: center;
  font-weight: 600;
  color: #999;
  padding-bottom: 8px;
  border-bottom: 2px solid #ddd;
  user-select: none;
}

.step.active {
  color: #c8ad7f;
  border-color: #c8ad7f;
}

h2 {
  text-align: center;
  margin-bottom: 24px;
}

fieldset {
  border: 1px solid #eee;
  padding: 16px;
  margin-bottom: 20px;
  border-radius: 6px;
}

legend {
  font-weight: bold;
  padding: 0 8px;
  color: #555;
}

.form-group {
  margin-bottom: 18px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
}

.required {
  color: #d9534f;
  margin-left: 4px;
}

input,
select {
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #bbb;
  border-radius: 4px;
  box-sizing: border-box;
}

input[type='date']::-webkit-calendar-picker-indicator {
  filter: invert(0.5);
}

.email-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.email-select {
  min-width: 140px;
  padding: 10px 12px;
}

.at-symbol {
  margin: 0 4px;
  font-weight: bold;
}

.input-with-btn {
  display: flex;
  gap: 8px;
}

.input-with-btn input {
  flex: 1;
}

.check-btn {
  background-color: #c8ad7f;
  border: none;
  border-radius: 4px;
  padding: 0 12px;
  color: white;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.check-btn:hover {
  background-color: #b79965;
}

.error-msg {
  color: #d9534f;
  font-weight: 700;
  margin-top: -12px;
  margin-bottom: 12px;
  text-align: center;
}

.pw-msg {
  margin-top: 4px;
  font-size: 13px;
  color: #d9534f;
}

.next-btn {
  margin-top: 24px;
  width: 100%;
  padding: 14px 0;
  background-color: #c8ad7f;
  border: none;
  border-radius: 6px;
  color: white;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.next-btn:hover {
  background-color: #b79965;
}

.hint {
  font-weight: normal;
  font-size: 12px;
  color: #666;
  margin-left: 6px;
}
</style>