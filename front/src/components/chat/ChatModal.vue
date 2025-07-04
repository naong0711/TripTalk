<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <button class="close-btn" @click="$emit('close')">✕</button>

      <div v-if="!selectedRoom">
        <!-- ✅ 이벤트로 roomId + receiverId 받기 -->
        <ChatList @selectRoom="enterRoom" />
      </div>
      <div v-else>
        <!-- ✅ 정확하게 바인딩 -->
        <ChatRoom
          :roomId="selectedRoom.roomId"
          :receiverId="selectedRoom.receiverId"
          :userRole="selectedRoom.userRole"
          :opponentName="selectedRoom.opponentName"
          @back="selectedRoom = null"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChatList from '@/components/chat/ChatList.vue'
import ChatRoom from '@/components/chat/ChatRoom.vue'

const selectedRoom = ref(null)

function enterRoom({ roomId, receiverId, userRole, opponentName }) {
  selectedRoom.value = { roomId, receiverId, userRole, opponentName }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content {
  background-color: white;
  border-radius: 10px;
  width: 500px;
  height: 600px;
  overflow: hidden;
  padding: 1rem;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 21px;
  right: 25px;
  background: transparent;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #555;
}

.close-btn:hover {
  color: #000;
}
</style>
