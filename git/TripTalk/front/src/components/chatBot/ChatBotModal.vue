<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <div class="chat-close-btn" @click="close">✕</div>
      <ItineraryBot />
    </div>
  </div>
</template>

<script setup>
import ItineraryBot from '@/views/board/ItineraryBot.vue'

defineProps({
  close: Function
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  position: relative;
  width: 650px;
  max-height: 90vh;
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.chat-close-btn {
  position: absolute;
  top: 34px;
  right: 38px;
  font-size: 1.5rem;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #ffffff;
  z-index: 10;
}

</style>
