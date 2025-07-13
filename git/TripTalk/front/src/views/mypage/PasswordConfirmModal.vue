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
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: white;
  padding: 24px;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
input {
  width: 94%;
  padding: 10px;
  margin-top: 12px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.buttons {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
button {
  padding: 8px 16px;
  border: none;
  background: #c8ad7f;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}
.save-btn:hover {
  background: #b4976f;
}
.error {
  margin-top: 10px;
  color: red;
  font-size: 14px;
}

.cancel-btn {
  background: #888;
  color: white;
  border: none;
  cursor: pointer;
}
</style>