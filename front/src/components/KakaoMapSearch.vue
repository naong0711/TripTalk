<template>
   <!-- ✅ 북마크 리스트는 지도 아래에 위치 -->
  <div class="bookmark-list" v-if="bookmarks.length > 0">
    <h3>📌 북마크 목록</h3>
    <ul>
      <li v-for="bookmark in bookmarks" :key="bookmark.id">
        {{ bookmark.placeName }} ({{ bookmark.address }})
        <button @click="deleteBookmark(bookmark.id)">❌</button>
      </li>
    </ul>
  </div>

 <div class="container">
    <!-- 여행기 제목/내용 입력 -->


    <!-- 지도/검색 UI -->
    <div class="map_wrap">
      <div id="map"></div>



      <div id="menu_wrap" class="bg_white">
        <div class="option">
          <form @submit.prevent="searchPlaces">
            <input type="text" v-model="keyword" placeholder="키워드를 입력하세요" size="15" />
            <button type="submit">검색</button>
          </form>
        </div>
        <hr />
        <ul id="placesList">
          <li
            v-for="(place, index) in places"
            :key="place.id || index"
            class="item"
            @mouseover="displayInfowindow(markers[index], place.place_name)"
            @mouseout="closeInfowindow"
          >
            <span :class="`markerbg marker_${index + 1}`"></span>
            <div class="info">
              <h5>{{ place.place_name }}</h5>
              <span>{{ place.road_address_name || place.address_name }}</span>
              <span v-if="place.road_address_name" class="jibun gray">{{ place.address_name }}</span>
              <span class="tel">{{ place.phone || '' }}</span>
              <button @click.stop="bookmarkPlace(place)">북마크</button>
            </div>
          </li>
        </ul>
        <div id="pagination">
          <a
            v-for="pageNum in pagination.last"
            :key="pageNum"
            href="#"
            :class="{ on: pageNum === pagination.current }"
            @click.prevent="gotoPage(pageNum)"
          >
            {{ pageNum }}
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

const route = useRoute()
const boardId = ref(route.query.id || null)
const title = ref('')
const content = ref('')
const keyword = ref('')
const userId = ref(null)
const bookmarks = ref([])

let map = null
let ps = null
let infowindow = null
let markers = []
let polyline = null

const places = ref([])
const pagination = ref({ current: 1, last: 1 })

async function fetchUserId() {
  try {
    const res = await axios.get('/api/user/me', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    userId.value = res.data.id
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
  }
}

onMounted(async () => {
  if (!window.kakao) {
    alert('카카오 지도 API가 로드되지 않았습니다. index.html에 스크립트 추가를 확인하세요.')
    return
  }

  await fetchUserId()

  window.kakao.maps.load(() => {
    initMap()
    if (userId.value) {
      loadBookmarksAndDraw()
    }
  })
})

function initMap() {
  const container = document.getElementById('map')
  map = new window.kakao.maps.Map(container, {
    center: new window.kakao.maps.LatLng(37.566826, 126.9786567),
    level: 3,
  })
  ps = new window.kakao.maps.services.Places()
  infowindow = new window.kakao.maps.InfoWindow({ zIndex: 1 })
}

function searchPlaces() {
  if (!keyword.value.trim()) {
    alert('키워드를 입력해주세요!')
    return
  }
  ps.keywordSearch(keyword.value, placesSearchCB)
}

function placesSearchCB(data, status, paginationData) {
  if (status === window.kakao.maps.services.Status.OK) {
    places.value = data
    pagination.value = paginationData
    displayMarkers(data)
  } else if (status === window.kakao.maps.services.Status.ZERO_RESULT) {
    alert('검색 결과가 존재하지 않습니다.')
    places.value = []
    clearMarkers()
  } else {
    alert('검색 중 오류가 발생했습니다.')
    places.value = []
    clearMarkers()
  }
}

function displayMarkers(placesArray) {
  clearMarkers()
  const bounds = new window.kakao.maps.LatLngBounds()

  placesArray.forEach((place, i) => {
    const position = new window.kakao.maps.LatLng(place.y, place.x)
    const marker = addMarker(position, i)
    markers.push(marker)
    bounds.extend(position)
  })

  map.setBounds(bounds)
}

function addMarker(position, idx) {
  const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png'
  const imageSize = new window.kakao.maps.Size(36, 37)
  const imgOptions = {
    spriteSize: new window.kakao.maps.Size(36, 691),
    spriteOrigin: new window.kakao.maps.Point(0, idx * 46 + 10),
    offset: new window.kakao.maps.Point(13, 37),
  }
  const markerImage = new window.kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions)
  const marker = new window.kakao.maps.Marker({ position, image: markerImage })
  marker.setMap(map)

  window.kakao.maps.event.addListener(marker, 'mouseover', () =>
    displayInfowindow(marker, places.value[idx].place_name)
  )
  window.kakao.maps.event.addListener(marker, 'mouseout', () => closeInfowindow())

  return marker
}

function clearMarkers() {
  markers.forEach((marker) => marker.setMap(null))
  markers = []
}

function displayInfowindow(marker, title) {
  infowindow.setContent(`<div style="padding:5px;">${title}</div>`)
  infowindow.open(map, marker)
}

function closeInfowindow() {
  infowindow.close()
}

function gotoPage(pageNum) {
  pagination.value.current = pageNum
  ps.keywordSearch(keyword.value, placesSearchCB, { page: pageNum })
}

async function submitTravelLog() {
 const data = {
    title: title.value,
    content: content.value,
    userId: userId.value, // userId 명확히 전달
    categoryId: 1, // 필요하면 동적으로 할당
  }
  try {
    const res = await fetch('/api/log/write', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })

    if (!res.ok) {
      alert('등록 실패!')
      return
    }

    const savedBoard = await res.json()
    const boardIdCreated = savedBoard.id
    const tempKey = localStorage.getItem('bookmarkTempKey')

    // 북마크 연결 요청
    if (tempKey) {
      await axios.put(`/api/map/link`, {
        boardId: boardIdCreated,
        tempKey,
      }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      })

      // 연결 후 tempKey 제거
      localStorage.removeItem('bookmarkTempKey')
    }

    alert('글이 성공적으로 등록되었습니다!')
    location.reload()
  } catch (err) {
    console.error('서버 오류:', err)
    alert('서버 오류!')
  }
}

async function bookmarkPlace(place) {
  if (!userId.value) {
    alert('로그인 후 이용 가능합니다.')
    return
  }

  // 없으면 새로 생성
  if (!localStorage.getItem('bookmarkTempKey')) {
    localStorage.setItem('bookmarkTempKey', crypto.randomUUID())
  }

  const bookmarkData = {
    placeName: place.place_name,
    address: place.road_address_name || place.address_name,
    latitude: Number(place.y),
    longitude: Number(place.x),
    userId: userId.value,
    tempKey: localStorage.getItem('bookmarkTempKey'), // 중요
    boardId: boardId.value,
  }

  try {
    await axios.post('/api/map', bookmarkData, {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })
    alert('북마크가 저장되었습니다!')
    loadBookmarksAndDraw()
  } catch (error) {
    console.error('북마크 저장 중 오류:', error)
    alert('북마크 저장에 실패했습니다.')
  }
}


async function loadBookmarksAndDraw() {
  const tempKey = localStorage.getItem('bookmarkTempKey')
  console.log(boardId.value)

  if (!boardId.value && !tempKey) return

   if (!boardId.value && !tempKey) {
    console.log('boardId와 tempKey 모두 없음, 북마크 불러오기 중단')
    return
  }

  let url = boardId.value
    ? `/api/map/get/${boardId.value}`
    : `/api/map/temp/${tempKey}` // 임시 북마크 조회용

  try {
    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },
    })

    bookmarks.value = res.data

    if (polyline) {
      polyline.setMap(null)
      polyline = null
    }

    polyline = drawPolyline(bookmarks.value)

    clearMarkers()
    bookmarks.value.forEach((bookmark) => {
      const marker = new window.kakao.maps.Marker({
        position: new window.kakao.maps.LatLng(bookmark.latitude, bookmark.longitude),
        map,
        title: bookmark.placeName,
      })
      markers.push(marker)
    })
  } catch (err) {
    console.error('북마크 불러오기 실패:', err)
  }
}


function drawPolyline(bookmarks) {
  if (!map || !bookmarks.length) return null

  const path = bookmarks.map((b) => new window.kakao.maps.LatLng(b.latitude, b.longitude))

  const polyline = new window.kakao.maps.Polyline({
    path,
    strokeWeight: 5,
    strokeColor: '#FF0000',
    strokeOpacity: 0.7,
    strokeStyle: 'solid',
  })

  polyline.setMap(map)

  const bounds = new window.kakao.maps.LatLngBounds()
  path.forEach((latlng) => bounds.extend(latlng))
  map.setBounds(bounds)

  return polyline
}

async function deleteBookmark(bookmarkId) {
  if (!confirm('정말 이 장소를 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/api/map/${bookmarkId}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
    },
  })
    // 삭제 후 북마크 다시 불러오기 (userId가 유효하면)
    if (userId.value) {
      await loadBookmarksAndDraw()
    }
    alert('삭제되었습니다.')
  } catch (err) {
    alert('삭제 실패했습니다.')
    console.error(err)
  }
}
</script>

<style scoped>
/* 기존 스타일 그대로 유지하세요 */
.map_wrap,
.map_wrap * {
  margin: 0;
  padding: 0;
  font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;
  font-size: 12px;
}

.map_wrap a,
.map_wrap a:hover,
.map_wrap a:active {
  color: #000;
  text-decoration: none;
}
.map_wrap {
  position: relative;
  width: 100%;
  height: 500px; /* 지도 높이 확보 필수! */
}
#map {
  width: 100%;
  height: 100%;
}
#menu_wrap {
  top: 0;
  left: 0;
  bottom: 0;
  width: 270px;
  margin: 10px 0 30px 10px;
  padding: 5px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.9);
  z-index: 1;
  font-size: 12px;
  border-radius: 10px;
}
.bg_white {
  background: #fff;
}
#menu_wrap hr {
  display: block;
  height: 1px;
  border: 0;
  border-top: 2px solid #5f5f5f;
  margin: 3px 0;
}
#menu_wrap .option {
  text-align: center;
}
#menu_wrap .option p {
  margin: 10px 0;
}
#menu_wrap .option button {
  margin-left: 5px;
}
#placesList {
  list-style: none;
  padding-left: 0;
}
#placesList .item {
  position: relative;
  border-bottom: 1px solid #888;
  overflow: hidden;
  cursor: pointer;
  min-height: 65px;
  padding-left: 55px;
}
#placesList .item span.markerbg {
  position: absolute;
  left: 10px;
  top: 15px;
  width: 36px;
  height: 37px;
  background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png')
    no-repeat;
}
#placesList .item .marker_1 {
  background-position: 0 -10px;
}
#placesList .item .marker_2 {
  background-position: 0 -56px;
}
#placesList .item .marker_3 {
  background-position: 0 -102px;
}
#placesList .item .marker_4 {
  background-position: 0 -148px;
}
#placesList .item .marker_5 {
  background-position: 0 -194px;
}
#placesList .item .marker_6 {
  background-position: 0 -240px;
}
#placesList .item .marker_7 {
  background-position: 0 -286px;
}
#placesList .item .marker_8 {
  background-position: 0 -332px;
}
#placesList .item .marker_9 {
  background-position: 0 -378px;
}
#placesList .item .marker_10 {
  background-position: 0 -423px;
}
#placesList .item .marker_11 {
  background-position: 0 -470px;
}
#placesList .item .marker_12 {
  background-position: 0 -516px;
}
#placesList .item .marker_13 {
  background-position: 0 -562px;
}
#placesList .item .marker_14 {
  background-position: 0 -608px;
}
#placesList .item .marker_15 {
  background-position: 0 -654px;
}
#placesList .item .info {
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
#placesList .item .info h5 {
  margin: 0;
}
#placesList .item .info .gray {
  color: #8a8a8a;
  padding-left: 26px;
  background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_jibun.png')
    no-repeat;
}
#placesList .item .info .tel {
  color: #009900;
  display: block;
}
#placesList .item .info button {
  margin-top: 5px;
  padding: 4px 8px;
  font-size: 12px;
}
#pagination {
  margin: 10px auto;
  text-align: center;
}
#pagination a {
  display: inline-block;
  margin-right: 10px;
  cursor: pointer;
}
#pagination a.on {
  font-weight: bold;
  cursor: default;
  color: #777;
}

.bookmark-list {
  background: #f9f9f9;
  border: 1px solid #ddd;
  padding: 10px 15px;
  margin-bottom: 20px;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto; /* 북마크가 많으면 스크롤 */
}
.bookmark-list h3 {
  margin-bottom: 10px;
}
.bookmark-list ul {
  list-style: none;
  padding-left: 0;
}
.bookmark-list li {
  margin-bottom: 8px;
  font-size: 14px;
}
.bookmark-list button {
  margin-left: 10px;
  cursor: pointer;
  background: transparent;
  border: none;
  color: red;
  font-weight: bold;
}

.container {
  padding: 0 5px; /* 양쪽 여백 주기 */
}

.map_wrap {
  width: 100%;
  height: 500px;
  display: flex;
  gap: 20px; /* 지도와 메뉴 사이 여백 */
}

#map {
  flex: 1;
  min-width: 60%; /* 메뉴랑 함께 보여질 공간 확보 */
}

#menu_wrap {
  width: 280px;
  max-height: 100%;
  overflow-y: auto;
}

.bookmark-list {
  background: #f9f9f9;
  border: 1px solid #ddd;
  padding: 10px 15px;
  margin: 20px auto; /* 가운데 정렬 */
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
  width: 720px; /* 내용 길이에 맞춰 너비 자동 조정 */
  min-width: 300px; /* 너무 작지 않도록 최소 너비 설정 */
  text-align: left; /* 내부 텍스트 가운데 정렬 */
}

#menu_wrap .option form {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
}

#menu_wrap .option input[type="text"] {
  flex: 1;
  padding: 6px 10px;
  font-size: 14px;
  border: 1.5px solid #4caf50;
  border-radius: 4px;
  outline: none;
}

#menu_wrap .option input[type="text"]:focus {
  border-color: #388e3c;
}

#menu_wrap .option button {
  padding: 6px 14px;
  font-size: 14px;
  background-color: #4caf50;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
}

#menu_wrap .option button:hover {
  background-color: #388e3c;
}

#menu_wrap .option button:active {
  background-color: #2e7d32;
}
</style>