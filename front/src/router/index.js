import { createRouter, createWebHistory } from 'vue-router'

import HelloWorld from '@/views/HelloWorld.vue'
import LoginForm from '@/views/login/LoginForm.vue'
import Reservation from '@/views/Reservation.vue'
import ReservationDetail from '@/views/mypage/ReservationDetail.vue'
import RegisterAgree from '@/views/register/RegisterAgree.vue'
import RegisterInfo from '@/views/register/RegisterInfo.vue'
import RegisterComplete from '@/views/register/RegisterComplete.vue'
import Mypage from '@/views/mypage/Mypage.vue'
import Profile from '@/views/mypage/Profile.vue'
import ProfileUpdate from '@/views/mypage/ProfileUpdate.vue'
import CartList from '@/views/mypage/CartList.vue'
import Favorite from '@/views/mypage/Favorite.vue'
import OAuthCallback from '@/views/login/OAuthCallback.vue'
import ProductList from '@/views/product/ProductList.vue'
import ProductRegister from '../views/product/ProductRegister.vue'
import ProductDetail from '../views/product/ProductDetail.vue'
import PaymentSuccess from '@/views/PaymentSuccess.vue'
import Boardlist from '@/views/board/BoardList.vue'
import BoardDetail from '../views/board/BoardDetail.vue'
import BoardWrite from '../views/board/BoardWrite.vue'
import SellerRegister from '../views/mypage/SellerRegister.vue'
import AdminPage from '../views/admin/AdminPage.vue'
import SellerRequests from '../views/admin/SellerRequests.vue'
import KakaoMapSearch from '../components/KakaoMapSearch.vue'
import MapOnly from '../views/MapOnly.vue'

const routes = [
  { path: '/', name: 'HelloWorld', component: HelloWorld } , //메인
  { path: '/loginForm', name: 'LoginForm', component: LoginForm } , //로그인
  { path: '/register/agree', name: 'RegisterAgree', component: RegisterAgree }, //회원가입폼(약관동의)
  { path: '/register/info', name: 'RegisterInfo', component: RegisterInfo }, //회원가입폼(가입정보입력)
  { path: '/register/complete', name: 'RegisterComplete', component: RegisterComplete }, //회원가입폼(완료페이지)
  { path: '/mypage', name: 'Mypage', component: Mypage }, //마이페이지
  { path: '/productDetail/:id', name: 'ProductDetail', component: ProductDetail }, //상품상세페이지
  { path: '/reservation', name: 'Reservation', component: Reservation }, // 상품리스트
  { path: '/mypage/reservationDetail/:id', name: 'reservationDetail', component: ReservationDetail }, // 예약상세페이지
  { path: '/mypage/profile', name: 'Profile', component: Profile }, // 내정보
  { path: '/mypage/update', name: 'ProfileUpdate', component: ProfileUpdate }, // 내정보 수정
  { path: '/cart', name: 'CartList', component: CartList }, //  장바구니 목록
  { path: '/favorite', name: 'Favorite', component: Favorite }, //  찜 목록
  { path: '/oauth2/callback', name: 'OAuthCallback', component: OAuthCallback}, //소셜로그인-카카오
  { path: '/productList', name: 'productList', component: ProductList},
  { path: '/productRegister', name: 'ProductRegister', component: ProductRegister},
  { path: '/productDetail/:id', name: 'ProductDetail', component: ProductDetail },
  { path: '/payment/success', name: 'PaymentSuccess', component: PaymentSuccess},
  { path: '/BoardList', name: 'BoardList', component: Boardlist},
  { path: '/BoardDetail/:id', name: 'BoardDetail', component: BoardDetail},
  { path: '/BoardWrite', name: 'BoardWrite', component: BoardWrite},
  { path: '/mypage/seller/request', name: 'SellerRegister', component: SellerRegister},
  { path: '/admin', name: 'AdminPage', component: AdminPage},
  { path: '/admin/sellerRequests', name: 'SellerRequests', component: SellerRequests},
  // { path: '/TravelLogRegister', name: 'TravelLogRegister', component: TravelLogRegister},
  { path: '/travelmap/:boardId', name: 'KakaoMapSearch', component: KakaoMapSearch,
    props: true },
  { path: '/MapOnly', name: 'MapOnly', component: MapOnly},
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router