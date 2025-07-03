import { createRouter, createWebHistory } from 'vue-router'

import HelloWorld from '@/views/HelloWorld.vue'
import LoginForm from '@/views/LoginForm.vue'
import Reservation from '@/views/Reservation.vue'
import RegisterAgree from '@/views/register/RegisterAgree.vue'
import RegisterInfo from '@/views/register/RegisterInfo.vue'
import RegisterComplete from '@/views/register/RegisterComplete.vue'
import Mypage from '@/views/mypage/Mypage.vue'
import ProductList from '@/views/ProductList.vue'
import ProductRegister from '../views/ProductRegister.vue'
import ProductDetail from '../views/ProductDetail.vue'

const routes = [
  { path: '/', name: 'HelloWorld', component: HelloWorld } , //메인
  { path: '/loginForm', name: 'LoginForm', component: LoginForm } , //로그인
  { path: '/register/agree', name: 'RegisterAgree', component: RegisterAgree }, //회원가입폼(약관동의)
  { path: '/register/info', name: 'RegisterInfo', component: RegisterInfo }, //회원가입폼(가입정보입력)
  { path: '/register/complete', name: 'RegisterComplete', component: RegisterComplete }, //회원가입폼(완료페이지)
  { path: '/mypage', name: 'Mypage', component: Mypage }, //마이페이지
  { path: '/reservation', name: 'Reservation', component: Reservation }, // 예약페이지
  { path: '/productList', name: 'productList', component: ProductList},
  { path: '/productRegister', name: 'ProductRegister', component: ProductRegister},
  { path: '/productDetail/:id', name: 'ProductDetail', component: ProductDetail },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router