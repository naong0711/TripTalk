import { createRouter, createWebHistory } from 'vue-router'

import HelloWorld from '@/views/HelloWorld.vue'
import LoginForm from '@/views/login/LoginForm.vue'
import Reservation from '@/views/Reservation.vue'
import RegisterAgree from '@/views/register/RegisterAgree.vue'
import RegisterInfo from '@/views/register/RegisterInfo.vue'
import RegisterComplete from '@/views/register/RegisterComplete.vue'
import Mypage from '@/views/mypage/Mypage.vue'
import Profile from '@/views/mypage/Profile.vue'
import ProfileUpdate from '@/views/mypage/ProfileUpdate.vue'
import CartList from '@/views/mypage/CartList.vue'
import Favorite from '@/views/mypage/Favorite.vue'
import OAuthCallback from '@/views/login/OAuthCallback.vue'
import ProductList from '@/views/ProductList.vue'
import ProductRegister from '../views/ProductRegister.vue'
import ProductDetail from '../views/ProductDetail.vue'
import PaymentSuccess from '@/views/PaymentSuccess.vue'


const routes = [
  { path: '/', name: 'HelloWorld', component: HelloWorld } , //메인
  { path: '/loginForm', name: 'LoginForm', component: LoginForm } , //로그인
  { path: '/register/agree', name: 'RegisterAgree', component: RegisterAgree }, //회원가입폼(약관동의)
  { path: '/register/info', name: 'RegisterInfo', component: RegisterInfo }, //회원가입폼(가입정보입력)
  { path: '/register/complete', name: 'RegisterComplete', component: RegisterComplete }, //회원가입폼(완료페이지)
  { path: '/mypage', name: 'Mypage', component: Mypage }, //마이페이지
  { path: '/reservation', name: 'Reservation', component: Reservation }, // 예약페이지
  { path: '/mypage/profile', name: 'Profile', component: Profile }, // 내정보
  { path: '/mypage/update', name: 'ProfileUpdate', component: ProfileUpdate }, // 내정보 수정
  { path: '/cart', name: 'CartList', component: CartList }, //  장바구니 목록
  { path: '/favorite', name: 'Favorite', component: Favorite }, //  찜 목록
  { path: '/oauth2/callback', name: 'OAuthCallback', component: OAuthCallback}, //소셜로그인-카카오
  { path: '/productList', name: 'productList', component: ProductList},
  { path: '/productRegister', name: 'ProductRegister', component: ProductRegister},
  { path: '/productDetail/:id', name: 'ProductDetail', component: ProductDetail },
  { path: '/payment/success', name: 'PaymentSuccess', component: PaymentSuccess},
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router