  <template>
    <div class="product-register-container">
  <div class="form-wrapper">
    <h2>여행 상품 등록</h2>

      <!-- 상품 정보 영역 -->
      <div class="section">
        <h3 class="section-title">상품 정보</h3>

        <div class="form-group">
          <label>제목 *</label>
          <input type="text" v-model="product.title" />
        </div>

        <div class="form-group">
          <label>설명 *</label>
          <input type="text" v-model="product.description" />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>지역 *</label>
            <select v-model="product.location">
              <option disabled value="">-- 지역을 선택하세요 --</option>
              <option>서울</option>
              <option>부산</option>
              <option>인천</option>
              <option>대구</option>
              <option>광주</option>
              <option>대전</option>
              <option>울산</option>
              <option>세종</option>
              <option>경기</option>
              <option>강원</option>
              <option>충북</option>
              <option>충남</option>
              <option>전북</option>
              <option>전남</option>
              <option>경북</option>
              <option>경남</option>
              <option>제주</option>
            </select>
          </div>

          <div class="form-group">
            <label>가격 *</label>
            <input type="number" v-model.number="product.price" min="1000" />
          </div>
        </div>

        <div class="form-group">
          <label>주소 *</label>
          <input type="text" v-model="product.address" />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>시작일 *</label>
            <input type="date" v-model="product.startDate" />
          </div>
          <div class="form-group">
            <label>종료일 *</label>
            <input type="date" v-model="product.endDate" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>최소 인원수 *</label>
            <input type="number" v-model.number="product.minPeople" min="1" />
          </div>
          <div class="form-group">
            <label>최대 인원수 *</label>
            <input type="number" v-model.number="product.maxPeople" :min="product.minPeople || 1" />
          </div>
        </div>

        <div class="form-group">
          <input type="hidden" v-model.number="product.categoryId" />
        </div>
      </div>

      <!-- 할인 정보 영역 -->
      <div class="section">
        <h3 class="section-title">할인 정보</h3>

        <div class="form-row">
          <div class="form-group">
            <label>할인 이름</label>
            <input type="text" v-model="product.discount.name" />
          </div>

          <div class="form-group">
            <label>할인율</label>
            <input type="number" v-model.number="product.discount.discountRate" min="0" max="100" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>할인 시작일</label>
            <input type="date" v-model="product.discount.startAt" />
          </div>

          <div class="form-group">
            <label>할인 종료일</label>
            <input type="date" v-model="product.discount.endAt" />
          </div>
        </div>
      </div>

      <!-- 이미지 업로드 영역 -->
      <div class="section">
        <h3 class="section-title">이미지 업로드</h3>
        <div class="form-row">
          <div class="form-group">
            <label>대표 이미지 *</label>
            <input type="file" accept="image/*" @change="onThumbnailChange" />
            <div v-if="thumbnailPreview" class="image-preview">
              <img :src="thumbnailPreview" alt="썸네일" />
            </div>
          </div>

        <div class="form-group">
          <label>추가 이미지</label>
          <input type="file" multiple accept="image/*" @change="onAdditionalChange" />
          <div class="additional-preview-list">
            <img v-for="(img, idx) in additionalPreviews" :key="idx" :src="img" />
          </div>
        </div>
      </div>
    </div>

    <!-- 버튼 -->
    <div class="button-group">
      <button class="submit-btn" @click="submitProduct">등록하기</button>
      <button class="cancel-btn" @click="cancel">취소</button>
    </div>
  </div>
  </div>

  </template>

  <script setup>
  import { ref, reactive, watch, defineProps, defineEmits ,onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  import CKEditor from '@ckeditor/ckeditor5-vue'
  import ClassicEditor from '@ckeditor/ckeditor5-build-classic'

  const CKEditorComponent = CKEditor.component 
  const editor = ClassicEditor

  const editorConfig = {
    toolbar: [ 'imageUpload' ],  // 이미지 업로드 버튼만 보여줌
    ckfinder: {
      uploadUrl: 'http://192.168.182.128/api/files/image/editor/'
    }
  }

  const router = useRouter()

  const product = reactive({
    title: '',
    description: '',
    address: '',
    location: '',
    price: null,
    startDate: '',
    endDate: '',
    sellerId: null,
    categoryId: 1,
    minPeople: null,
    maxPeople: null,  
    discount: {
      discountType: 'RATE',
      name: '',
      discountRate: null,
      startAt: '',
      endAt: ''
    }
  })

  // 🔽 sellerId를 서버에서 불러오기
  onMounted(async () => {
    try {
      const response = await axios.get('/api/sellers/my-id', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      })
      product.sellerId = response.data
      console.log('📦 sellerId:', product.sellerId)
    } catch (err) {
      console.error('❌ sellerId 조회 실패', err)
      alert('판매자 정보 조회에 실패했습니다.')
    }
  })

  const props = defineProps({
    thumbnail: File,
    additionalImages: Array
  })

  const emit = defineEmits(['update:thumbnail', 'update:additionalImages'])

  const thumbnailPreview = ref(null)
  const thumbnailFile = ref(null)
  const additionalPreviews = ref([])

  const onThumbnailChange = (e) => {
    const file = e.target.files[0]
    if (file) {
      thumbnailFile.value = file  
      emit('update:thumbnail', file)
      thumbnailPreview.value = URL.createObjectURL(file)
    }
  }

  const onAdditionalChange = (e) => {
    const files = e.target.files
    if (files.length > 0) {
      const arr = Array.from(files)
      emit('update:additionalImages', arr)
      additionalPreviews.value = arr.map(f => URL.createObjectURL(f))
    }
  }

  watch(() => props.thumbnail, (newVal) => {
    if (newVal) thumbnailPreview.value = URL.createObjectURL(newVal)
  })

  watch(() => props.additionalImages, (newVal) => {
    if (newVal && newVal.length) {
      additionalPreviews.value = newVal.map(f => URL.createObjectURL(f))
    }
  })
  const toIso = (dateStr) => {
    if (!dateStr) return null
    return new Date(dateStr).toISOString().replace('Z', '')
  }
const submitProduct = async () => {
  if (!product.title || !product.description || !product.address|| !product.location  || !product.price ||
    !product.startDate || !product.endDate || !product.sellerId || !product.categoryId ||
    !product.minPeople || !product.maxPeople) {
    alert('필수 항목을 모두 입력해주세요.')
    return
  }
  if (product.minPeople > product.maxPeople) {
    alert('최소 인원수가 최대 인원수보다 클 수 없습니다.')
    return
  }
  if (!thumbnailFile.value) {
    alert('대표 이미지를 선택해주세요.')
    return
  }

    try {
      const adjustedProduct = {
        ...product,
        startDate: toIso(product.startDate),
        endDate: toIso(product.endDate),
        discount: product.discount.discountRate ? {
          discountType: product.discount.discountType,
          discountRate: product.discount.discountRate / 100,
          name: product.discount.name,
          startAt: toIso(product.discount.startAt),
          endAt: toIso(product.discount.endAt)
        } : null
      }

    const formData = new FormData()
    formData.append('product', JSON.stringify(adjustedProduct))
    formData.append('file', thumbnailFile.value)

    // ✅ 헤더에 Authorization 추가
    await axios.post('/api/product', formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'multipart/form-data'
      }
    })

    alert('상품 등록 성공')
    router.push('/productList')
  } catch (error) {
    console.error(error)
    alert('상품 등록 실패')
  }
}

  const cancel = () => {
    router.push('/productList')
  }
  </script>

  <style scoped>
/* 상품 등록 전체 컨테이너 */
.product-register-container {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
  background-color: #f8f9fa;
}

/* 폼 박스 */
.form-wrapper {
  width: 100%;
  max-width: 720px;
  background: #ffffff;
  padding: 36px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  font-family: 'Noto Sans KR', sans-serif;
}

/* 제목 */
h2 {
  font-size: 24px;
  margin-bottom: 28px;
  text-align: center;
  font-weight: 700;
  color: #2c2c2c;
}

/* 각 폼 필드 그룹 */
.form-group {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
}

/* 라벨 스타일 */
label {
  font-weight: 600;
  margin-bottom: 6px;
  color: #444;
}

/* 입력창 공통 스타일 */
input[type="text"],
input[type="number"],
input[type="date"],
input[type="file"],
select,
textarea {
  padding: 10px 12px;
  font-size: 14px;
  border: 1.5px solid #bbb;
  border-radius: 6px;
  transition: border-color 0.3s ease;
  background-color: #fff;
}

/* 포커스 효과 */
input:focus,
select:focus,
textarea:focus {
  border-color: #4c589b;
  outline: none;
}

/* 텍스트에어리어 */
textarea {
  resize: vertical;
  min-height: 100px;
}

/* 이미지 미리보기 */
.image-preview img,
.additional-preview-list img {
  max-width: 100%;
  max-height: 160px;
  border-radius: 8px;
  object-fit: cover;
  margin-top: 10px;
  border: 1px solid #ddd;
  padding: 4px;
  background-color: #fff;
}

/* 추가 이미지 미리보기 리스트 */
.additional-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 32px;
}

/* 제출 버튼 */
.submit-btn,
.cancel-btn {
  flex: 1;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background 0.3s ease;
}

/* 등록 버튼 */
.submit-btn {
  background-color: #292e4c;
  color: white;
}

.submit-btn:hover {
  background-color: #1d2138;
}

/* 취소 버튼 */
.cancel-btn {
  background-color: #e0e0e0;
  color: #333;
}

.cancel-btn:hover {
  background-color: #cfcfcf;
}

/* 셀렉트 드롭다운 커스텀 */
select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg fill='%23666' height='20' viewBox='0 0 24 24' width='20' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M7 10l5 5 5-5z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px 16px;
  background-color: #fff;
}

/* 파일 인풋 여백 제거 */
input[type="file"] {
  padding: 6px 0;
}

.section {
  border: 1px solid #e0e0e0;
  padding: 24px;
  margin-bottom: 32px;
  border-radius: 8px;
  background-color: #fafafa;
}

/* 섹션 제목 */
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #333;
  border-left: 4px solid #292e4c;
  padding-left: 12px;
}

.form-row {
  display: flex;
  gap: 20px;
  justify-content: space-between;
}

/* form-group이 form-row 안에 있을 때 너비 조정 */
.form-row .form-group {
  flex: 1;
}
  </style>
