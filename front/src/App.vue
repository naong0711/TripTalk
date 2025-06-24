<template>
  <div id="app">
    <Header /> <!-- ✅ 모든 페이지 상단에 항상 보임 -->

    <main>
    <router-view /> <!-- 각 페이지의 내용 -->
    </main>

    
  </div>
</template>

<script setup>

import Header from '@/components/Header.vue' // ✅ 헤더 컴포넌트 임포트

</script>

<style>
/* 전역 스타일 정의 가능 */
body {
  margin: 0;
  font-family: 'Arial', sans-serif;
  background-color: #f9f9f9;
}
</style>