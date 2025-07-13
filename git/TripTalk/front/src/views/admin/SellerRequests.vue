<template>
  <div class="seller-request-container">
    <h2>판매자 신청 목록</h2>

    <table class="request-table">
      <thead>
        <tr>
          <th>판매자 ID</th>
          <th>판매자 이름</th>
          <th>첨부파일</th>
          <th>승인</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="request in requests" :key="request.id">
          <td>{{ request.sellerId }}</td>
          <td>{{ request.sellerName }}</td>
          <td>
            <span class="file-link" @click="openImageModal(request.id, request.fileName)">
              {{ request.fileName }}
            </span>
          </td>
          <td>
            <label class="switch">
                <input type="checkbox" :checked="request.status" @change="toggleApproval(request.id)" />
                <span class="slider round"></span>
            </label>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- ✅ 이미지 모달 -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <img :src="modalImageUrl" alt="첨부 이미지" />
        <p>{{ modalFileName }}</p>
        <button class="close-btn" @click="closeModal">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const requests = ref([])
const showModal = ref(false)
const modalImageUrl = ref('')
const modalFileName = ref('')

onMounted(async () => {
  try {
    const res = await axios.get('/api/sellers/list')
    requests.value = res.data
    console.log( requests.value)
  } catch (err) {
    console.error('판매자 신청 목록 로드 실패:', err)
    alert('데이터를 불러오지 못했습니다.')
  }
})


function openImageModal(id, fileName) {
  modalImageUrl.value = `/api/files/image/SELLER/${id}`
  modalFileName.value = fileName
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function toggleApproval(id) {
  try {
    await axios.post(`/api/sellers/approve/${id}`)

    // ✅ id로 해당 request 객체를 찾아서 상태를 반전
    const target = requests.value.find(r => r.id === id)
    if (target) {
      target.status = !target.status
    }
  } catch (err) {
    console.error('승인 상태 변경 실패:', err)
    alert('승인 상태 변경 실패')
  }
}

</script>


<style scoped>
.seller-request-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 24px;
  background-color: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 8px;
}

h2 {
  margin-bottom: 20px;
  font-size: 22px;
}

.request-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.request-table th,
.request-table td {
  padding: 12px;
  border: 1px solid #ccc;
  text-align: center;
}

.request-table th {
  background-color: #f5f5f5;
  font-weight: 600;
}

.file-link {
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}

button {
  padding: 6px 12px;
  margin: 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

button:hover:not(:disabled) {
  background-color: #eaeaea;
}

button:disabled {
  background-color: #ddd;
  cursor: not-allowed;
}

/* ✅ 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  max-width: 90%;
  max-height: 90%;
  overflow: auto;
}

.modal-content img {
  max-width: 100%;
  max-height: 400px;
  margin-bottom: 12px;
  border: 1px solid #ccc;
}

.close-btn {
  background-color: #c8ad7f;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #28a745; /* 초록: 승인됨 */
}

input:checked + .slider:before {
  transform: translateX(22px);
}
</style>
