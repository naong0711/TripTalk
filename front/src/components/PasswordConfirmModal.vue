<template>
  <div class="modal-backdrop">
    <div class="modal">
      <h3>비밀번호 확인</h3>
      <input
        type="password"
        v-model="password"
        placeholder="비밀번호를 입력하세요"
      />
      <div class="buttons">
        <button class="cancel-btn" @click="$emit('cancel')">취소</button>
        <button class="save-btn" @click="confirm">확인</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const password = ref('')
const error = ref('')

const emit = defineEmits(['confirm', 'cancel'])

async function confirm() {
  try {
    await axios.post('/api/user/verify-password', { password: password.value }, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })
    emit('confirm')
  } catch (err) {
    error.value = '비밀번호가 일치하지 않습니다.'
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
