<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <button class="close-btn" @click="$emit('close')">✕</button>

      <div v-if="!localRoom">
        <ChatList @selectRoom="enterRoom" />
      </div>
      <div v-else>
        <ChatRoom
          :roomId="localRoom.roomId"
          :receiverId="localRoom.receiverId"
          :userRole="localRoom.userRole"
          :opponentName="localRoom.opponentName"
          @back="localRoom = null"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import ChatList from '@/components/chat/ChatList.vue'
import ChatRoom from '@/components/chat/ChatRoom.vue'

const props = defineProps({
  selectedRoom: Object
})

const emit = defineEmits(['close', 'selectRoom'])

const localRoom = ref(props.selectedRoom || null)

// 
watch(() => props.selectedRoom, (val) => {
  localRoom.value = val
})


function enterRoom(roomInfo) {
  emit('selectRoom', roomInfo)
  localRoom.value = roomInfo
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
  display: flex; 
  flex-direction: column;
}

.close-btn {
  position: absolute;
  top: 21px;
  right: 32px;
  background: transparent;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #555;
  z-index: 20;
}

.close-btn:hover {
  color: #000;
}

.modal-content > div {
  flex: 1;
  overflow-y: auto;
}
</style>
