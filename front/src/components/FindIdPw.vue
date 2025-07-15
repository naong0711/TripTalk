<template>
  <div class="modal-overlay" @click.self="closeModal">
    <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="modal-title">
      <button class="close-btn" @click="closeModal" aria-label="닫기">×</button>
      <h2 id="modal-title">아이디 / 비밀번호 찾기</h2>

      <div class="tabs">
        <button :class="{ active: mode === 'id' }" @click="mode = 'id'">아이디 찾기</button>
        <button :class="{ active: mode === 'pw' }" @click="mode = 'pw'">비밀번호 찾기</button>
      </div>

      <form @submit.prevent="mode === 'id' ? findId() : findPassword()">
        <div v-if="mode === 'id'" class="form-group">
          <label for="find-id-input">이메일</label>
          <input
            id="find-id-input"
            type="text"
            v-model="findIdInput"
            placeholder="이메일 입력"
            required
            autocomplete="username"
          />
        </div>

        <div v-if="mode === 'pw'">
          <div class="form-group">
            <label for="find-pw-id">아이디</label>
            <input
              id="find-pw-id"
              type="text"
              v-model="findPwId"
              placeholder="아이디 입력"
              required
              autocomplete="username"
            />
          </div>
          <div class="form-group">
            <label for="find-pw-email">가입 시 등록한 이메일</label>
            <input
              id="find-pw-email"
              type="email"
              v-model="findPwEmail"
              placeholder="이메일 입력"
              required
              autocomplete="email"
            />
          </div>
        </div>

        <button type="submit" :disabled="loading" class="submit-btn">
          {{ loading ? (mode === 'id' ? '조회중...' : '처리중...') : (mode === 'id' ? '아이디 찾기' : '비밀번호 재설정 요청') }}
        </button>
      </form>

      <p v-if="resultMessage" :class="['result-message', errorMsg ? 'error' : '']">
        {{ resultMessage }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const emit = defineEmits(['close'])

const mode = ref('id')
const findIdInput = ref('')
const findPwId = ref('')
const findPwEmail = ref('')

const resultMessage = ref('')
const errorMsg = ref(false)
const loading = ref(false)

function closeModal() {
  emit('close')
  resetForm()
}

function resetForm() {
  findIdInput.value = ''
  findPwId.value = ''
  findPwEmail.value = ''
  resultMessage.value = ''
  errorMsg.value = false
  loading.value = false
}

async function findId() {
  loading.value = true
  resultMessage.value = ''
  errorMsg.value = false

try {
  await new Promise(resolve => setTimeout(resolve, 1000))

  const res = await axios.post('/api/user/findId', { query: findIdInput.value })
  const userId = res.data.userId

  let maskedId = ''

  if (userId.length <= 1) {
    maskedId = '*'
  } else if (userId.length === 2) {
    maskedId = userId[0] + '*'
  } else if (userId.length === 3) {
    maskedId = userId[0] + '*' + userId[2]
  } else {
    const head = userId.substring(0, 2)
    const tail = userId.slice(-1)
    const stars = '*'.repeat(userId.length - 3)
    maskedId = head + stars + tail
  }

  resultMessage.value = `회원님의 아이디는\n[ ${maskedId} ] 입니다.`
} catch (e) {
  errorMsg.value = true
  resultMessage.value = '아이디 찾기에 실패했습니다.'
} finally {
  loading.value = false
}
}

async function findPassword() {
  loading.value = true
  resultMessage.value = ''
  errorMsg.value = false

  try {
    // 실제 API 호출 부분 예시
    await new Promise(resolve => setTimeout(resolve, 1000))

    await axios.post('/api/user/findPw', { userId: findPwId.value, email: findPwEmail.value })

    resultMessage.value = '비밀번호 재설정 이메일을 발송했습니다. 이메일을 확인하세요.'
  } catch (e) {
    errorMsg.value = true
    resultMessage.value = '비밀번호 찾기에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0,0,0,0.3);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background: #fff;
  width: 360px;
  max-width: 90vw;
  padding: 32px 32px 36px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  font-family: 'Noto Sans KR', sans-serif;
  position: relative;
  text-align: left;
  user-select: none;
}

/* 닫기 버튼 */
.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 28px;
  color: #bbb;
  background: none;
  border: none;
  cursor: pointer;
  line-height: 1;
  transition: color 0.2s ease;
}
.close-btn:hover {
  color: #292e4c;
}

/* 제목 */
h2 {
  font-weight: 800;
  font-size: 24px;
  color: #292e4c;
  margin-bottom: 24px;
  text-align: center;
  letter-spacing: 0.02em;
}

/* 탭 스타일 */
.tabs {
  display: flex;
  justify-content: center;
  gap: 28px;
  margin-bottom: 32px;
  border-bottom: 2px solid #e0e4ec;
}

.tabs button {
  background: none;
  border: none;
  font-weight: 600;
  font-size: 16px;
  padding-bottom: 8px;
  cursor: pointer;
  color: #999;
  border-bottom: 3px solid transparent;
  transition: color 0.3s ease, border-bottom-color 0.3s ease;
}

.tabs button.active {
  color: #292e4c;
  border-bottom-color: #292e4c;
}

/* form-group */
.form-group {
  margin-bottom: 20px;
}

label {
  font-weight: 600;
  font-size: 14px;
  color: #2c2c2c;
  margin-bottom: 6px;
  display: block;
}

/* input 필드 */
input[type='text'],
input[type='email'] {
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #bbb;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.2s ease;
  outline: none;
  color: #222;
}

input::placeholder {
  color: #bbb;
}

input[type='text']:focus,
input[type='email']:focus {
  border-color: #292e4c;
}

/* 제출 버튼 */
.submit-btn {
  width: 100%;
  background-color: #292e4c;
  border: none;
  border-radius: 8px;
  padding: 14px 0;
  font-weight: 700;
  font-size: 16px;
  color: white;
  cursor: pointer;
  transition: background-color 0.25s ease;
  user-select: none;
}

.submit-btn:disabled {
  background-color: #bbb;
  cursor: not-allowed;
  color: #eee;
}

.submit-btn:hover:not(:disabled) {
  background-color: #1d2138;
}

/* 결과 메시지 */
.result-message {
  margin: 20px auto 0;
  max-width: 90%;
  font-weight: 600;
  font-size: 15px;
  text-align: center;
  white-space: pre-wrap;
  background-color: #f4f8e8;
  border: 1px solid #c8d6b9;
  border-radius: 12px;
  padding: 14px 16px;
  color: #4b692f;
  box-shadow: 0 4px 10px rgba(183, 199, 134, 0.3);
  transition: all 0.3s ease;
  word-break: keep-all;
}

.result-message.error {
  background-color: #ffe8e8;
  border: 1px solid #ffcccc;
  color: #d32f2f;
  box-shadow: 0 4px 10px rgba(255, 170, 170, 0.3);
}
</style>
