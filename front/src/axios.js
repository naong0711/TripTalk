import axios from 'axios'
import router from '@/router'

const instance = axios.create({
  baseURL: ''


})
instance.interceptors.response.use(
  response => response,
  async error => {
    console.warn('🟡 인터셉터 진입:', error?.response?.status, error?.config?.url)

    const originalRequest = error.config

    if (
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry
    ) {
      console.log('🔄 401 감지됨. 리프레시 시도...')
      originalRequest._retry = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        console.warn('❌ 리프레시 토큰 없음')
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        router.push('/loginForm')
        return Promise.reject(error)
      }

      try {
        const res = await instance.post('/api/user/login/refresh', null, {
          headers: { Authorization: `Bearer ${refreshToken}` }
        })

        console.log('📦 res.data:', res.data)
        const newAccessToken = res.data.accessToken
        console.log('✅ 새 accessToken 발급:', newAccessToken)

        localStorage.setItem('accessToken', newAccessToken)
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`

        return instance(originalRequest)

      } catch (refreshErr) {
        console.error('🔴 리프레시 토큰 갱신 실패:', refreshErr)
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        router.push('/loginForm')
        return Promise.reject(refreshErr)
      }
    }

    return Promise.reject(error)
  }
)

export default instance
