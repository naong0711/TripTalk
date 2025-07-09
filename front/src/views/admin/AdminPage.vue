<template>
  <div class="admin-container">
    <div class="admin-header">
      <h2>관리자 페이지</h2>
      <p>{{ adminName }}</p>
    </div>

    <div class="admin-sections">
      <div class="admin-card" @click="goToUserList">
        <h3>회원 관리</h3>
        <p>전체 회원 정보 조회 및 차단 관리</p>
      </div>
      <div class="admin-card" @click="goToProductList">
        <h3>상품 관리</h3>
        <p>등록된 상품 목록 및 삭제 처리</p>
      </div>
      <div class="admin-card" @click="goToReportCenter">
        <h3>신고 관리</h3>
        <p>사용자 신고 접수 및 처리</p>
      </div>
      <div class="admin-card" @click="goToSellerRequests">
        <h3>판매자 신청 관리</h3>
        <p>판매자 신청 목록 조회 및 승인/거절</p>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const adminName = ref('관리자님') // 로그인 시 받아오는 정보로 대체 가능

function goToUserList() {
  router.push('/admin/users')
}
function goToProductList() {
  router.push('/admin/products')
}
function goToReportCenter() {
  router.push('/admin/reports')
}
function goToSellerRequests() {
  router.push('/admin/sellerRequests')
}
</script>

<style scoped>
.admin-container {
  max-width: 960px;
  margin: 40px auto;
  padding: 24px;
  font-family: 'Noto Sans KR', sans-serif;
}

.admin-header {
  margin-bottom: 32px;
  text-align: center;
}

.admin-header h2 {
  font-size: 28px;
  margin-bottom: 8px;
}

.admin-sections {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.admin-card {
  background-color: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 24px;
  cursor: pointer;
  transition: box-shadow 0.2s ease;
}

.admin-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.admin-card h3 {
  margin-bottom: 10px;
  color: #3c3c3c;
}

.admin-card p {
  font-size: 14px;
  color: #666;
}
</style>
