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
  top: 0; left: 0;
  width: 100%; height: 100%;
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
  display: flex;
  flex-direction: column;
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
  cursor: pointer;
}
</style>
