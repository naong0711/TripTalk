<template>
  <div class="modal-backdrop">
    <div class="modal">
      <h3>비밀번호 변경</h3>
      
      <input
        type="password"
        v-model="newPassword"
        placeholder="새 비밀번호를 입력하세요"
        autocomplete="new-password"
      />
      <input
        type="password"
        v-model="confirmPassword"
        placeholder="새 비밀번호 확인"
        autocomplete="new-password"
        @keyup.enter="submit"
      />
      
      <p v-if="error" class="error">{{ error }}</p>
      
      <div class="buttons">
        <button class="cancel-btn" @click="$emit('cancel')">취소</button>
        <button class="save-btn" @click="submit">변경</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')

const emit = defineEmits(['success', 'cancel'])

async function submit() {
  error.value = ''

  if (!newPassword.value || !confirmPassword.value) {
    error.value = '새 비밀번호와 확인란을 모두 입력해주세요.'
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    error.value = '비밀번호가 일치하지 않습니다.'
    return
  }

try {
  await axios.post('/api/user/changePassword', {
    newPassword: newPassword.value,
    confirmPassword: confirmPassword.value,
  }, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('accessToken')}`
    }
  })

  emit('success') // 여기 이후의 로직에서 오류 발생 시 catch로 감싸야 함

} catch (err) {
  console.error('비밀번호 변경 오류:', err)
  error.value = err.response?.data?.message || '비밀번호 변경에 실패했습니다.'
}
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  padding: 32px 32px 36px;
  border-radius: 12px;
  max-width: 360px;
  width: 100%;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  font-family: 'Noto Sans KR', sans-serif;
  display: flex;
  flex-direction: column;
  text-align: left;
}

h3 {
  font-weight: 800;
  font-size: 24px;
  color: #292e4c;
  margin-bottom: 24px;
  text-align: center;
}

input {
  width: 100%;
  padding: 10px 12px;
  margin-top: 14px;
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

input:focus {
  border-color: #292e4c;
}

.buttons {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

button {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.25s ease;
  color: white;
}

.save-btn {
  background-color: #292e4c;
}

.save-btn:hover {
  background-color: #1d2138;
}

.cancel-btn {
  background-color: #888;
}

.cancel-btn:hover {
  background-color: #666;
}

.error {
  margin-top: 16px;
  color: #d32f2f;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
}
</style>
