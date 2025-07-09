<template>
  <div class="seller-request-container">
    <h2>판매자 신청</h2>

    <div class="form-wrapper">
      <form @submit.prevent="submitRequest">
        <!-- 은행명 + 예금주 한 줄 -->
        <div class="form-row">
          <div class="form-group small">
            <label>은행명</label>
            <select v-model="bankName" required>
              <option disabled value="">선택</option>
              <option>국민은행</option>
              <option>신한은행</option>
              <option>우리은행</option>
              <option>하나은행</option>
              <option>농협은행</option>
              <option>카카오뱅크</option>
              <option>기업은행</option>
              <option>SC제일은행</option>
              <option>씨티은행</option>
              <option>토스뱅크</option>
            </select>
          </div>

          <div class="form-group small">
            <label>예금주</label>
            <input v-model="accountHolder" required />
          </div>
        </div>

        <div class="form-group">
          <label>계좌번호</label>
          <input v-model="accountNumber" required />
        </div>

        <div class="form-group">
          <label>사업자 등록증 이미지 *</label>
          <input type="file" accept="image/*" @change="onThumbnailChange" required />
          <div v-if="thumbnailPreview" class="preview">
            <img :src="thumbnailPreview" alt="썸네일" />
          </div>
        </div>

        <div class="button-wrapper">
          <button type="submit" class="submit-btn">신청하기</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const bankName = ref('')
const accountNumber = ref('')
const accountHolder = ref('')

const thumbnailFile = ref(null)
const thumbnailPreview = ref(null)

const onThumbnailChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    thumbnailFile.value = file
    thumbnailPreview.value = URL.createObjectURL(file)
  }
}

async function submitRequest() {
  if (!thumbnailFile.value) return alert('파일을 첨부해주세요.')

  const formData = new FormData()
  formData.append('bankName', bankName.value)
  formData.append('accountNumber', accountNumber.value)
  formData.append('accountHolder', accountHolder.value)
  formData.append('file', thumbnailFile.value)

  try {
    const res = await axios.post('/api/sellers/apply', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })

    alert('판매자 신청이 완료되었습니다. 승인 이후 판매등록이 가능합니다. (예상소요기간 : 2-3일)')
    router.push('/mypage')
  } catch (err) {
    console.error(err)
    alert('신청 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
.seller-request-container {
  max-width: 480px;
  margin: 40px auto;
  padding: 0 24px;
  font-family: 'Noto Sans KR', sans-serif;
}

.form-wrapper {
  background-color: #faf7f2;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.form-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}

.form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.form-group.small {
  flex: 1;
}

label {
  margin-bottom: 4px;
  font-weight: 500;
}

input,
select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
}

.preview img {
  width: 100%;
  max-height: 300px;
  object-fit: contain;
  margin-top: 8px;
  border: 1px solid #ddd;
}

.button-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.submit-btn {
  background-color: #6c8e3f;
  color: white;
  padding: 10px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s ease;
}

.submit-btn:hover {
  background-color: #5a7533;
}
</style>
