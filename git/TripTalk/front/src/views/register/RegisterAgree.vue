<template>
  <div class="register-agree">
    <!-- 단계 표시 UI -->
    <div class="stepper">
      <div :class="['step', { active: currentStep === 1 }]">1. 약관 동의</div>
      <div :class="['step', { active: currentStep === 2 }]">2. 회원 정보 입력</div>
      <div :class="['step', { active: currentStep === 3 }]">3. 완료</div>
    </div>

    <h2>회원가입 약관 동의</h2>

    <form @submit.prevent="goNext">
      <div v-for="(term, idx) in terms" :key="term.id" class="term-item">
        <label class="custom-checkbox">
          <input type="checkbox" v-model="term.checked" />
          <span class="checkmark"></span>
          <span class="term-title">{{ term.title }}</span>
        </label>
        <hr />
        <div class="term-content">{{ term.content }}</div>
      </div>

      <div class="agree-all">
        <label class="custom-checkbox">
          <input type="checkbox" v-model="allAgree" @change="toggleAllAgree" />
          <span class="checkmark"></span>
          <span>모두 동의합니다.</span>
        </label>
      </div>

      <button type="submit" :disabled="!canProceed" class="next-btn">
        다음으로
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const currentStep = 1

const terms = reactive([
  {
    id: 1,
    title: '서비스 이용약관(필수)',
    content: `제1조 (목적)
본 약관은 회사가 제공하는 서비스의 이용과 관련하여 회사와 이용자의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.

제2조 (용어의 정의)
1. "서비스"라 함은 회사가 제공하는 온라인상에서의 모든 서비스를 의미합니다.
2. "이용자"라 함은 본 약관에 따라 회사가 제공하는 서비스를 받는 회원 및 비회원을 말합니다.

제3조 (약관의 명시와 개정)
회사는 본 약관의 내용을 이용자가 쉽게 알 수 있도록 서비스 초기 화면에 게시하며, 약관을 개정할 경우 변경된 약관의 내용을 공지합니다.

(중략)
`,
    checked: false,
  },
  {
    id: 2,
    title: '개인정보 처리방침(필수)',
    content: `1. 개인정보의 수집 및 이용 목적
회사는 회원 가입, 서비스 제공, 고객 상담 등을 위하여 개인정보를 수집하며, 이를 아래와 같은 목적을 위해 사용합니다.

2. 수집하는 개인정보 항목
- 이름, 이메일, 연락처, 주소 등 회원 관리에 필요한 정보

3. 개인정보의 보유 및 이용 기간
회사는 개인정보 수집 및 이용 목적이 달성되면 해당 정보를 지체 없이 파기합니다.

4. 개인정보의 제3자 제공
회사는 이용자의 동의 없이 개인정보를 외부에 제공하지 않습니다.

(중략)
`,
    checked: false,
  },
  {
    id: 3,
    title: '마케팅 정보 수신 동의(선택)',
    content: `본인은 회사가 제공하는 프로모션, 이벤트, 할인 정보 등의 마케팅 정보를 수신하는 데 동의합니다. 동의하지 않을 경우 서비스 이용에 제한이 없으며, 언제든지 수신 거부를 할 수 있습니다.

- 수신 방법: 이메일, SMS, 푸시 알림 등
- 수신 거부 방법: 마케팅 정보 수신 동의 해제 페이지에서 직접 설정하거나 고객센터에 문의

(중략)
`,
    checked: false,
  },
])

const allAgree = ref(false)

function toggleAllAgree() {
  terms.forEach(term => {
    term.checked = allAgree.value
  })
}

watch(
  () => terms.map(term => term.checked),
  (newVals) => {
    allAgree.value = newVals.every(Boolean)
  },
  { deep: true }
)

const canProceed = computed(() => {
  return terms.filter(term => term.id !== 3).every(term => term.checked)
})

const router = useRouter()
function goNext() {
  if (!canProceed.value) return
  router.push('/register/info')
}
</script>

<style scoped>
.register-agree {
  max-width: 520px;
  margin: 48px auto;
  padding: 32px;
  background-color: #ffffff;
  border: 1px solid #e0e4ec;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  font-family: 'Noto Sans KR', sans-serif;
}

.stepper {
  display: flex;
  justify-content: space-between;
  margin-bottom: 36px;
}

.step {
  flex: 1;
  text-align: center;
  font-weight: 600;
  color: #c0c0c0;
  padding-bottom: 10px;
  border-bottom: 3px solid #e0e0e0;
  font-size: 15px;
  transition: color 0.3s, border-color 0.3s;
}

.step.active {
  color: #292e4c;
  border-color: #292e4c;
}

h2 {
  text-align: center;
  font-size: 22px;
  margin-bottom: 24px;
  color: #2c2c2c;
}

.term-item {
  background-color: #f9f9fb;
  border: 1px solid #dadce0;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 20px;
}

.term-item hr {
  border: none;
  border-top: 1px solid #d1d5db;
  margin: 10px 0 14px;
}

.term-content {
  font-size: 14px;
  color: #444;
  white-space: pre-wrap;
  height: 120px;
  overflow-y: auto;
  padding-right: 6px;
  line-height: 1.6;
}

.agree-all {
  margin-top: 28px;
  padding: 14px 20px;
  background: #fefaf3;
  border-top: 1px solid #d1d5db;
  border-radius: 8px;
}

.custom-checkbox {
  position: relative;
  display: flex;
  align-items: center;
  padding-left: 32px;
  cursor: pointer;
  user-select: none;
  font-weight: 600;
  color: #333;
}

.custom-checkbox input[type="checkbox"] {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.custom-checkbox .checkmark {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  height: 20px;
  width: 20px;
  background-color: #fff;
  border: 2px solid #6b7280;
  border-radius: 4px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.custom-checkbox input[type="checkbox"]:checked ~ .checkmark {
  background-color: #4b5563;
  border-color: #4b5563;
}

.custom-checkbox .checkmark::after {
  content: "";
  position: absolute;
  display: none;
  left: 5px;
  bottom: 4px;
  width: 6px;
  height: 11px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.custom-checkbox input[type="checkbox"]:checked ~ .checkmark::after {
  display: block;
}

.term-title {
  font-size: 15px;
  margin-left: 6px;
}

.next-btn {
  margin-top: 32px;
  width: 100%;
  padding: 14px 0;
  background-color: #2f354f;
  border: none;
  border-radius: 8px;
  color: #fff;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.next-btn:hover:not(:disabled) {
  background-color: #23293e;
}

.next-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
