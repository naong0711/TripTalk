  <template>
    <div class="product-register-container">
      <div class="form-wrapper">
        <h2>신규 상품 등록</h2>

        <div class="form-group">
          <label>제목 *</label>
          <input type="text" v-model="product.title" />
        </div>

        <div class="form-group">
          <label>설명 *</label>
          <input type="text" v-model="product.description" />
        </div>

        <div class="form-group">
          <label>주소 *</label>
          <input type="text" v-model="product.address" />
        </div>

        <div class="form-group">
          <label>가격 *</label>
          <input type="number" v-model.number="product.price" min="1000" />
        </div>

        <div class="form-group">
          <label>시작일 *</label>
          <input type="datetime-local" v-model="product.startDate" />
        </div>

        <div class="form-group">
          <label>종료일 *</label>
          <input type="datetime-local" v-model="product.endDate" />
        </div>

        <div class="form-group">
          <label>판매자 ID *</label>
          <input type="number" v-model.number="product.sellerId" />
        </div>

        <div class="form-group">
          <label>카테고리 ID *</label>
          <input type="number" v-model.number="product.categoryId" />
        </div>

        <div class="form-group">
          <label>할인 타입</label>
          <select v-model="product.discount.discountType">
            <option value="RATE">비율 할인 (RATE)</option>
            <option value="AMOUNT">금액 할인 (AMOUNT)</option>
          </select>
        </div>

        <div class="form-group">
          <label>할인 이름</label>
          <input type="text" v-model="product.discount.name" />
        </div>

        <div class="form-group">
          <label>할인율</label>
          <input type="number" v-model.number="product.discount.discountRate" min="0" max="100" />
        </div>

        <div class="form-group">
          <label>할인 시작일</label>
          <input type="datetime-local" v-model="product.discount.startAt" />
        </div>

        <div class="form-group">
          <label>할인 종료일</label>
          <input type="datetime-local" v-model="product.discount.endAt" />
        </div>

        <!-- <div class="form-group">
          <label>대표 이미지 (썸네일) *</label>
          <input type="file" @change="onThumbnailSelected" accept="image/*" />
          <div v-if="thumbnailPreview" class="image-preview">
            <img :src="thumbnailPreview" alt="썸네일 미리보기" />
          </div>
        </div>


        <div class="form-group">
          <label>추가 이미지 (선택)</label>
          <input type="file" multiple @change="onAdditionalImagesSelected" accept="image/*" />
          <div class="image-preview-list">
            <img v-for="(img, idx) in additionalPreviews" :key="idx" :src="img" />
          </div>
        </div> -->

          <div>
      <label>대표 이미지 *</label>
      <input type="file" accept="image/*" @change="onThumbnailChange" />
      <div v-if="thumbnailPreview">
        <img :src="thumbnailPreview" alt="썸네일" />
      </div>

      <label>추가 이미지</label>
      <input type="file" multiple accept="image/*" @change="onAdditionalChange" />
      <div class="additional-preview-list">
        <img v-for="(img, idx) in additionalPreviews" :key="idx" :src="img" />
      </div>
    </div>

        <div class="button-group">
          <button class="submit-btn" @click="submitProduct">등록하기</button>
          <button class="cancel-btn" @click="cancel">취소</button>
        </div>
      </div>
    </div>
  </template>

  <script setup>
  import { ref, reactive, watch, defineProps, defineEmits } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  import CKEditor from '@ckeditor/ckeditor5-vue'
  import ClassicEditor from '@ckeditor/ckeditor5-build-classic'

  const CKEditorComponent = CKEditor.component 
  const editor = ClassicEditor

  const editorConfig = {
    toolbar: [ 'imageUpload' ],  // 이미지 업로드 버튼만 보여줌
    ckfinder: {
      uploadUrl: 'http://localhost:8080/api/files/image/editor/'
    }
  }

  const router = useRouter()

  const product = reactive({
    title: '',
    description: '',
    address: '',
    price: null,
    startDate: '',
    endDate: '',
    sellerId: null,
    categoryId: null,
    discount: {
      discountType: 'RATE',
      name: '',
      discountRate: null,
      startAt: '',
      endAt: ''
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
    if (!product.title || !product.description || !product.address || !product.price ||
      !product.startDate || !product.endDate || !product.sellerId || !product.categoryId) {
      alert('필수 항목을 모두 입력해주세요.')
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
          discountRate: product.discount.discountRate,
          name: product.discount.name,
          startAt: toIso(product.discount.startAt),
          endAt: toIso(product.discount.endAt)
        } : null
      }

      const formData = new FormData()
      formData.append('product', JSON.stringify(adjustedProduct))
      formData.append('file', thumbnailFile.value)

      await axios.post('/api/product', formData)

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
  /* 기존 스타일 유지 */
  .product-register-container {
    display: flex;
    justify-content: center;
    padding: 40px 20px;
    background-color: #f8f9fa;
  }
  .form-wrapper {
    width: 100%;
    max-width: 700px;
    background: white;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 0 12px rgba(0,0,0,0.08);
  }
  h2 {
    font-size: 24px;
    margin-bottom: 24px;
    text-align: center;
  }
  .form-group {
    margin-bottom: 16px;
    display: flex;
    flex-direction: column;
  }
  label {
    font-weight: 600;
    margin-bottom: 6px;
  }
  input[type="text"],
  input[type="number"],
  input[type="datetime-local"],
  textarea {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
  }
  textarea {
    resize: vertical;
  }
  .image-preview img {
    max-width: 100%;
    max-height: 160px;
    margin-top: 10px;
    border-radius: 6px;
    border: 1px solid #ddd;
    object-fit: cover;
  }
  .image-preview-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 10px;
  }
  .image-preview-list img {
    width: 80px;
    height: 80px;
    border-radius: 6px;
    border: 1px solid #ddd;
    object-fit: cover;
  }
  .button-group {
    display: flex;
    justify-content: space-between;
    margin-top: 24px;
  }
  .submit-btn,
  .cancel-btn {
    flex: 1;
    padding: 12px 20px;
    font-size: 16px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: background 0.3s ease;
  }
  .submit-btn {
    background-color: #00c73c;
    color: white;
    margin-right: 12px;
  }
  .cancel-btn {
    background-color: #e0e0e0;
  }
  </style>

  <style scoped>
  /* 스타일은 기존 그대로 유지 */
  .product-register-container {
    display: flex;
    justify-content: center;
    padding: 40px 20px;
    background-color: #f8f9fa;
  }
  .form-wrapper {
    width: 100%;
    max-width: 700px;
    background: white;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 0 12px rgba(0,0,0,0.08);
  }
  h2 {
    font-size: 24px;
    margin-bottom: 24px;
    text-align: center;
  }
  .form-group {
    margin-bottom: 16px;
    display: flex;
    flex-direction: column;
  }
  .form-group-row {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
  }
  .form-group.half {
    flex: 1;
  }
  label {
    font-weight: 600;
    margin-bottom: 6px;
  }
  input[type="text"],
  input[type="number"],
  input[type="datetime-local"],
  textarea {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
  }
  textarea {
    resize: vertical;
  }
  .image-preview img {
    max-width: 100%;
    max-height: 160px;
    margin-top: 10px;
    border-radius: 6px;
    border: 1px solid #ddd;
    object-fit: cover;
  }
  .image-preview-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 10px;
  }
  .image-preview-list img {
    width: 80px;
    height: 80px;
    border-radius: 6px;
    border: 1px solid #ddd;
    object-fit: cover;
  }
  .button-group {
    display: flex;
    justify-content: space-between;
    margin-top: 24px;
  }
  .submit-btn,
  .cancel-btn {
    flex: 1;
    padding: 12px 20px;
    font-size: 16px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: background 0.3s ease;
  }
  .submit-btn {
    background-color: #00c73c;
    color: white;
    margin-right: 12px;
  }
  .cancel-btn {
    background-color: #e0e0e0;
  }
  </style>
