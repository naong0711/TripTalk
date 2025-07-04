<template>
  <div class="profile-detail">
    <h2>회원 상세 정보</h2>

    <div class="form-group">
      <label for="userId">아이디</label>
      <input id="userId" v-model.trim="form.userId" type="text" placeholder="아이디" readonly />
    </div>

    <div class="form-group">
      <label for="password">비밀번호</label>
      <input id="password" v-model="form.password" type="password" placeholder="비밀번호 입력" />
    </div>

    <div class="form-group">
      <label for="passwordConfirm">비밀번호 확인</label>
      <input id="passwordConfirm" v-model="form.passwordConfirm" type="password" placeholder="비밀번호 확인" />
      <p v-if="form.password && form.passwordConfirm && form.password !== form.passwordConfirm" class="pw-msg">
        비밀번호가 일치하지 않습니다.
      </p>
      <p v-else-if="form.password && !isPasswordValid" class="pw-msg">
        비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다.
      </p>
    </div>

    <div class="form-group">
      <label for="name">이름</label>
      <input id="name" v-model.trim="form.name" type="text" placeholder="이름을 입력하세요" />
    </div>

    <div class="form-group">
      <label for="nickname">닉네임</label>
      <input id="nickname" v-model.trim="form.nickname" type="text" placeholder="닉네임을 입력하세요" />
    </div>

    <div class="form-group">
    <label>이메일</label>
    <div class="email-group">
        <input v-model.trim="form.emailId" type="text" placeholder="이메일 아이디" />
        <span class="at-symbol">@</span>
        <select v-model="form.emailDomain" required>
        <option value="">선택</option>
        <option value="gmail.com">gmail.com</option>
        <option value="naver.com">naver.com</option>
        <option value="daum.net">daum.net</option>
        <option value="kakao.com">kakao.com</option>
        <option value="direct">직접 입력</option>
        </select>
    </div>

    <div v-if="form.emailDomain === 'direct'" style="margin-top: 8px">
        <input v-model.trim="form.customEmailDomain" type="text" placeholder="도메인을 입력하세요 (예: example.com)" />
    </div>

    <div v-if="isEmailChanged" class="email-verify">
        <template v-if="emailVerified">
            <span style="color: green; font-weight: 600;">✔ 이메일 인증이 완료되었습니다.</span>
        </template>
        <template v-else>
            <span class="pw-msg">이메일 변경됨 — 인증 필요</span>
            <button class="link-btn" @click="verifyEmail">인증 요청</button>
        </template>
    </div>

    <div v-if="isEmailChanged && !emailVerified" class="pw-msg">
    ⚠ 이메일 인증이 완료되지 않았습니다.
    </div>
    </div>

    <div class="form-group">
      <label for="phone">전화번호</label>
      <input id="phone" v-model.trim="form.phone" type="tel" placeholder="전화번호를 입력하세요" />
    </div>

    <div class="form-group">
      <label for="birthDate">생년월일</label>
      <input id="birthDate" v-model="form.birthDate" type="date" />
    </div>

    <div class="form-group">
      <label>주소</label>
      <div class="input-with-btn">
        <input v-model="form.zipcode" type="text" placeholder="우편번호" readonly />
        <button type="button" class="check-btn" @click="searchAddress">주소 찾기</button>
      </div>
      <input v-model="form.address" type="text" placeholder="도로명주소" style="margin-top: 8px" />
      <input v-model="form.addressDetail" type="text" placeholder="상세주소를 입력하세요" style="margin-top: 8px" />
    </div>

    <div class="button-group">
      <button class="cancel-btn" @click="goBack">취소</button>
      <button class="save-btn" @click="submitUpdate">저장</button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

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

const isPasswordValid = computed(() => {
  const pw = form.password
  return pw.length >= 8 && /[A-Za-z]/.test(pw) && /\d/.test(pw) && /[^A-Za-z0-9]/.test(pw)
})

const originalEmail = ref('')
const emailVerified = ref(true)

const currentEmail = computed(() => {
  return form.emailDomain === 'direct'
    ? `${form.emailId}@${form.customEmailDomain}`
    : `${form.emailId}@${form.emailDomain}`
})

const isEmailChanged = computed(() => currentEmail.value !== originalEmail.value)


// ✅ 이메일 인증 상태 확인
async function getEmailValid() {
  try {
    const res = await axios.get('/api/email/validate', {
      params: { userId: form.userId }
    })
    emailVerified.value = res.data === 1
  } catch (err) {
    console.error('이메일 인증 상태 확인 실패:', err)
    emailVerified.value = false
  }
}

// ✅ 이메일 변경 감지 → 인증 상태 초기화 또는 확인
watch(currentEmail, async (newVal) => {
  if (newVal !== originalEmail.value) {
    emailVerified.value = false
  } else {
    await getEmailValid()
  }
})

let intervalId = null

// ✅ 인증 이메일 재발송
async function verifyEmail() {
  try {
    await axios.post('/api/email/changeEmail', {
      userId: form.userId,
      email: currentEmail.value
    })
    alert('인증 메일이 재발송되었습니다.')

    // 인증 상태 주기적 확인 (5초마다)
    if (intervalId) clearInterval(intervalId) // 중복 방지

    intervalId = setInterval(async () => {
      await getEmailValid()

      // 인증 완료되면 중단
      if (emailVerified.value) {
        clearInterval(intervalId)
        intervalId = null
      }
    }, 5000)
  } catch (error) {
    console.error('이메일 인증 요청 실패:', error)
    alert('메일 재발급에 실패했습니다.')
  }
}

// ✅ 페이지 로드시 정보 불러오기 + 초기 인증 상태 확인
onMounted(async () => {
  try {
    const res = await axios.get('/api/mypage/profile', {
      headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    })

    const data = res.data
    form.userId = data.userId
    form.name = data.name ?? ''
    form.nickname = data.nickname ?? ''
    form.phone = data.phone ?? ''
    form.birthDate = data.birthDate ?? ''
    form.zipcode = data.zipcode ?? ''
    form.address = data.address ?? ''
    form.addressDetail = data.addressDetail ?? ''

    if (data.email) {
      const [id, domain] = data.email.split('@')
      form.emailId = id
      form.emailDomain = ['gmail.com', 'naver.com', 'daum.net', 'kakao.com'].includes(domain) ? domain : 'direct'
      form.customEmailDomain = form.emailDomain === 'direct' ? domain : ''
      originalEmail.value = data.email

      // ✅ 이메일 인증 상태 초기 확인
      await getEmailValid()
    }
    await getEmailValid()

        if (!emailVerified.value) {
        if (intervalId) clearInterval(intervalId)

        intervalId = setInterval(async () => {
            await getEmailValid()
            if (emailVerified.value) {
            clearInterval(intervalId)
            intervalId = null
            }
        }, 5000)
    }
  } catch (error) {
    console.error('회원 정보 불러오기 실패:', error)
  }
})

// ✅ 주소 검색
function searchAddress() {
  new window.daum.Postcode({
    oncomplete: function (data) {
      form.zipcode = data.zonecode
      form.address = data.roadAddress || data.jibunAddress
    }
  }).open()
}

// ✅ 정보 저장
async function submitUpdate() {
  if (form.password !== form.passwordConfirm) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  if (form.password && !isPasswordValid.value) {
    alert('비밀번호 형식을 확인해주세요.')
    return
  }

  if (isEmailChanged.value && !emailVerified.value) {
    alert('이메일 인증이 필요합니다.')
    return
  }

  const email = currentEmail.value

  const updateData = {
    userId: form.userId,
    name: form.name,
    nickname: form.nickname,
    email: currentEmail.value,
    phone: form.phone,
    birthDate: form.birthDate,
    zipcode: form.zipcode,
    address: form.address,
    addressDetail: form.addressDetail,
    ...(form.password ? { password: form.password } : {})
  }

  try {
    const res = await axios.put('/api/mypage/profile/update', updateData, {
      headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    })
      alert('정보가 수정되었습니다.')
      router.back()
  } catch (err) {
    console.error('업데이트 실패:', err)
    alert('정보 수정에 실패했습니다.')
  }
}

function goBack() {
  router.back()
}
</script>



<style scoped>
.profile-detail {
  max-width: 480px;
  margin: 50px auto;
  padding: 30px 32px;
  border-radius: 16px;
  background: #fafafa;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

h2 {
  font-weight: 700;
  font-size: 1.8rem;
  margin-bottom: 28px;
  text-align: center;
  color: #5a4e3a;
}

.form-group {
  margin-bottom: 22px;
}

label {
  font-weight: 600;
  display: block;
  margin-bottom: 8px;
  color: #6b5e3a;
  letter-spacing: 0.02em;
}

input,
select {
  width: 100%;
  padding: 12px 14px;
  font-size: 15px;
  border: 1.8px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
  background-color: #fff;
  box-sizing: border-box;
}

input:focus,
select:focus {
  outline: none;
  border-color: #a88a5e;
  box-shadow: 0 0 6px rgba(168, 138, 94, 0.5);
  background-color: #fffef6;
}

.email-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.at-symbol {
  font-size: 20px;
  color: #a88a5e;
  font-weight: 700;
}

.pw-msg {
  color: #d9534f;
  font-size: 14px;
  margin-top: 6px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.input-with-btn {
  display: flex;
  gap: 12px;
  align-items: center;
}

.input-with-btn input[type="text"] {
  flex: 1 1 140px;  /* 최소 너비 140px 이상, 가능하면 넓게 */
  min-width: 140px;
  padding: 12px 14px;
  font-size: 15px;
  border: 1.8px solid #ccc;
  border-radius: 8px 0 0 8px; /* 왼쪽만 둥글게 */
  background-color: #fff;
  box-sizing: border-box;
}


@media (max-width: 520px) {
  .profile-detail {
    margin: 20px 15px;
    padding: 24px 20px;
  }

  button,
  .check-btn {
    padding: 12px 0;
  }
}

.check-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 16px; /* 상하 여백을 키움 */
  min-width: 110px;    /* 최소 너비를 지정 */
  height: 46px;        /* input 높이와 맞춤 */
  background: #c8ad7f;
  border: none;
  border-radius: 0 8px 8px 0; /* 오른쪽만 둥글게 */
  color: white;
  font-weight: 700;
  cursor: pointer;
  font-size: 15px;
  gap: 6px;
}

.button-group {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

/* 저장 버튼 */
.save-btn {
  flex: 1;
  padding: 14px 0;
  font-size: 17px;
  background:#c8ad7f;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.cancel-btn {
  flex: 1;
  padding: 14px 0;
  font-size: 17px;
  background: #888;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.email-verify {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.link-btn {
  background: none;
  border: none;
  color: #007acc;
  cursor: pointer;
  padding: 0;
  font-weight: 600;
  font-size: 14px;
  text-decoration: underline;
}

.link-btn:hover {
  color: #004b8a;
}
</style>