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
            <!-- <span :class="`markerbg marker_${index + 1}`"></span> -->
             <span
                class="markerbg"
                :style="getMarkerStyle(index)"
              ></span>
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

function getMarkerStyle(index) {
  const offsetY = -10 - index * 46 // 시작 위치가 -10px, 간격은 46px
  return {
    background: `url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png') no-repeat`,
    width: '36px',
    height: '37px',
    backgroundPosition: `0px ${offsetY}px`,
    display: 'inline-block',
  }
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
  const spriteSize = new window.kakao.maps.Size(36, 691)
  const spriteOriginY = (idx * 46) + 10

  const imgOptions = {
    spriteSize: spriteSize,
    spriteOrigin: new window.kakao.maps.Point(0, spriteOriginY),
    offset: new window.kakao.maps.Point(13, 37),
  }

  const markerImage = new window.kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions)

  const marker = new window.kakao.maps.Marker({
    position,
    image: markerImage,
  })

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
/* 지도 영역 공통 */
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
  color: #292e4c;
  text-decoration: none;
}

.map_wrap {
  position: relative;
  width: 100%;
  height: 500px;
  display: flex;
}

#map {
  flex: 1;
  min-width: 60%;
  height: 100%;
}

#menu_wrap {
  width: 280px;
  max-height: 100%;
  overflow-y: auto;
  margin: 10px 0 30px 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  z-index: 1;
  font-size: 12px;
  border: 1.5px solid #d1d5db;
}

.bg_white {
  background: #fff;
}

#menu_wrap hr {
  display: block;
  height: 1px;
  border: 0;
  border-top: 2px solid #292e4c;
  margin: 8px 0;
}

#menu_wrap .option {
  text-align: center;
}

#menu_wrap .option p {
  margin: 10px 0;
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
  border: 1.5px solid #292e4c;
  border-radius: 4px;
  outline: none;
}

#menu_wrap .option input[type="text"]:focus {
  border-color: #1d2138;
  box-shadow: 0 0 6px rgba(41, 46, 76, 0.3);
}

#menu_wrap .option button {
  padding: 6px 14px;
  font-size: 14px;
  background-color: #292e4c;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

#menu_wrap .option button:hover {
  background-color: #1d2138;
}

#menu_wrap .option button:active {
  background-color: #141828;
}

/* 장소 리스트 */
#placesList {
  list-style: none;
  padding-left: 0;
}

#placesList .item {
  position: relative;
  border-bottom: 1px solid #bbb;
  overflow: hidden;
  cursor: pointer;
  min-height: 65px;
  padding-left: 70px;
  background-color: #fafbff;
  border-radius: 4px;
  margin-bottom: 6px;
  padding: 10px;
}

#placesList .item:hover {
  background-color: #e9ecf5;
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

#placesList .item .info {
  text-overflow: ellipsis;
  overflow: hidden;
  margin-left: 45px;
}

#placesList .item .info h5 {
  margin: 0;
  color: #292e4c;
  font-weight: 600;
  font-size: 14px;
}

#placesList .item .info .gray {
  color: #6b7280;
  padding-left: 26px;
  background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_jibun.png') no-repeat;
}

#placesList .item .info .tel {
  color: #292e4c;
  display: block;
}

#placesList .item .info button {
  margin-top: 5px;
  padding: 4px 8px;
  font-size: 12px;
  background-color: #292e4c;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

#placesList .item .info button:hover {
  background-color: #1d2138;
}

#pagination {
  margin: 10px auto;
  text-align: center;
}

#pagination a {
  display: inline-block;
  margin-right: 10px;
  cursor: pointer;
  color: #292e4c;
}

#pagination a.on {
  font-weight: bold;
  cursor: default;
  color: #1d2138;
}

/* 북마크 리스트 */
.bookmark-list {
  background: #f4f6fa;
  border: 1px solid #ccc;
  padding: 12px 18px;
  margin: 20px auto;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
  width: 680px;
  min-width: 300px;
  color: #2c2c2c;
  font-size: 14px;
}

.bookmark-list h3 {
  margin-bottom: 10px;
  color: #292e4c;
  font-weight: bold;
}

.bookmark-list ul {
  list-style: none;
  padding-left: 0;
}

.bookmark-list li {
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.bookmark-list button {
  background: none;
  border: none;
  color: #d32f2f;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
}

.bookmark-list button:hover {
  color: #b71c1c;
}

</style>