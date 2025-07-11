package org.kosa.tripTalk.bookmark;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.kosa.tripTalk.travellog.TravelLog;
import org.kosa.tripTalk.travellog.TravelLogRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final UserRepository userRepository;
    private final TravelLogRepository travelLogRepository;
    

    @Value("${kakao.api.key}")
    private String apiKey;

    // 키워드로 장소 검색
    public String searchPlaces(String keyword) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    // 북마크 저장하기 (boardId도 함께 저장)
    @Transactional
    public void saveBookMark(BookMarkDTO bookMarkDTO, Long userId) {
        System.out.println("saveBookMark 호출 boardId: " + bookMarkDTO.getBoardId() + ", userId: " + userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        BookMark.BookMarkBuilder builder = BookMark.builder()
            .placeName(bookMarkDTO.getPlaceName())
            .address(bookMarkDTO.getAddress())
            .latitude(bookMarkDTO.getLatitude())
            .longitude(bookMarkDTO.getLongitude())
            .tempKey(bookMarkDTO.getTempKey() != null ? bookMarkDTO.getTempKey() : UUID.randomUUID().toString())
            .user(user);

        try {
        	if (bookMarkDTO.getBoardId() != null) {
                TravelLog travelLog = travelLogRepository.findById(bookMarkDTO.getBoardId())
                    .orElseThrow(() -> new IllegalArgumentException("여행기 없음"));
                builder.board(travelLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("북마크 저장 중 TravelLog 조회 오류", e);
        }

        bookMarkRepository.save(builder.build());
    }
    
    // 특정 userId + boardId 기준 북마크 목록 반환 (필요하면)
    public List<BookMarkResponseDTO> getBookMarkDTOsByUserIdAndBoardId(Long userId, Long boardId) {
    	TravelLog board = travelLogRepository.findById(boardId).orElseThrow();
    	List<BookMark> bookmarks = bookMarkRepository.findByUserIdAndBoardOrderByCreatedAtAsc(userId, board);
    	
        return bookmarks.stream()
                .map(b -> new BookMarkResponseDTO(
                        b.getId(),
                        b.getPlaceName(),
                        b.getAddress(),
                        b.getLatitude(),
                        b.getLongitude(),
                        b.getTempKey()))
                .toList();
    }

    // 단순 userId 기준 북마크 목록
    public List<BookMarkResponseDTO> getBookMarkDTOs(Long userId) {
        List<BookMark> bookmarks = bookMarkRepository.findByUserIdOrderByCreatedAtAsc(userId);
        return bookmarks.stream()
                .map(b -> new BookMarkResponseDTO(
                        b.getId(),
                        b.getPlaceName(),
                        b.getAddress(),
                        b.getLatitude(),
                        b.getLongitude(),
                        b.getTempKey()))
                .toList();
    }
    
    public List<BookMarkDTO> findByTempKey(String tempKey) {
        return bookMarkRepository.findByTempKey(tempKey).stream()
                .map(BookMarkDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public void linkBookmarksToBoard(Long boardId, String tempKey) {
        TravelLog travelLog = travelLogRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("여행기 없음"));

        List<BookMark> bookmarks = bookMarkRepository.findByTempKey(tempKey);
        for (BookMark bookmark : bookmarks) {
            bookmark.setBoard(travelLog); // ✅ TravelLog 엔티티로 설정
            bookmark.setTempKey(null);    // tempKey 제거
        }
        System.out.println("goodluck");
        bookMarkRepository.saveAll(bookmarks);
    }

    @Transactional
    public void deleteBookmark(Long bookmarkId, Long userId) {
        BookMark bookmark = bookMarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("북마크가 존재하지 않습니다."));

        if (!bookmark.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 북마크를 삭제할 권한이 없습니다.");
        }

        bookMarkRepository.delete(bookmark);
    }

    public List<BookMarkDTO> findByBoardId(Long boardId) {
        List<BookMark> entities = bookMarkRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
        List<BookMarkDTO> dtos = entities.stream()
            .map(BookMarkDTO::fromEntity)
            .collect(Collectors.toList());
        return dtos;
    }
    
    
}
