package org.kosa.tripTalk.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.common.dto.PageRequestDTO;
import org.kosa.tripTalk.common.dto.Search;
import org.kosa.tripTalk.exception.NotFoundException;
import org.kosa.tripTalk.seller.Seller;
import org.kosa.tripTalk.seller.SellerRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("상품 테스트")
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProductServiceTest {

	@Autowired
	ProductService productService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	
	private User savedUser;
    private Seller savedSeller;
    private Category savedCategory;

	@BeforeEach
    void setup() {
        savedUser = userRepository.save(User.builder()
                .userId("user123")
                .name("홍길동")
                .email("user@test.com")
                .password("1234")
                .nickname("길동이")
                .role(User.Role.SELLER)
                .phone("010-1234-5678")
                .build());

        savedSeller = sellerRepository.save(Seller.builder()
                .user(savedUser)
                .userid(savedUser.getUserId())         // 별도로 저장하는 문자열
                .businessNumber("111-22-33333")
                .businessName("길동상점")
                .contact("070-1234-5678")
                .build());

        savedCategory = categoryRepository.save(Category.builder()
                .kind("여행")
                .description("여행 카테고리")
                .iconUrl("https://example.com/default.png")
                .build());
    }
	
	@Test
    @DisplayName("정상 상품 등록")
    void createProduct_success() {
        // given
        ProductRequestDTO request = ProductRequestDTO.builder()
                .title("제주도 여행")
                .description("힐링 3박 4일")
                .address("제주특별자치도 제주시")
                .price(299000)
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(6))
                .sellerId(savedUser.getId())
                .categoryId(savedCategory.getId())
                .build();

        // when
        productService.create(request);

        // then
        List<Product> result = productRepository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("제주도 여행");
    }

	@Test
	@DisplayName("상품 정상 수정")
	void updateProduct_success() {
	    // given
	    Product savedProduct = productRepository.save(Product.builder()
	            .title("기존 제목")
	            .description("기존 설명")
	            .address("서울시 강남구")
	            .price(100000)
	            .startDate(LocalDateTime.now().plusDays(5))
	            .endDate(LocalDateTime.now().plusDays(7))
	            .category(savedCategory)
	            .seller(savedSeller)
	            .build());

	    ProductRequestDTO updateDto = ProductRequestDTO.builder()
	            .title("수정된 제목")
	            .description("수정된 설명")
	            .address("부산 해운대")
	            .price(200000)
	            .startDate(LocalDateTime.now().plusDays(10))
	            .endDate(LocalDateTime.now().plusDays(13))
	            .build();

	    // when
	    productService.update(savedProduct.getId(), updateDto);

	    // then
	    Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
	    assertThat(updatedProduct.getTitle()).isEqualTo("수정된 제목");
	    assertThat(updatedProduct.getAddress()).isEqualTo("부산 해운대");
	    assertThat(updatedProduct.getPrice()).isEqualTo(200000);
	}
	
	@Test
	@DisplayName("상품 목록 페이징 + 정렬 + 검색 조회 성공")
	void getAllProducts_withPagingSortingAndSearch_success() {
	    // given: 15개의 상품 등록
	    for (int i = 1; i <= 15; i++) {
	        productRepository.save(Product.builder()
	                .title("제주 상품 " + i)
	                .description("힐링 설명 " + i)
	                .address("주소" + i)
	                .price(10000 * i)
	                .startDate(LocalDateTime.now().plusDays(i))
	                .endDate(LocalDateTime.now().plusDays(i + 2))
	                .category(savedCategory)
	                .seller(savedSeller)
	                .build());
	    }

	    PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 10, "price,asc");
	    Pageable pageable = pageRequestDTO.toPageable();

	    // 검색 조건: title에 "제주" 포함
		Search search = new Search();
	    search.setSearchKey("title");
	    search.setSearchValue("제주");

	    // when
	    Page<ProductResponseDTO> result = productService.getAllProducts(pageable, search);

	    // then
	    assertThat(result.getContent()).hasSize(10);                 // 1페이지에 10개
	    assertThat(result.getTotalElements()).isEqualTo(15);         // 총 15개 중 모두 "제주" 포함
	    assertThat(result.getTotalPages()).isEqualTo(2);             // 총 2페이지
	    assertThat(result.isFirst()).isTrue();                       // 첫 페이지
	    assertThat(result.isLast()).isFalse();                       // 아직 마지막 아님
	    assertThat(result.getContent().get(0).getPrice()).isEqualTo(10000); // 정렬 확인 (오름차순)
	}
	
	@DisplayName("상품 조회 실패 - 존재하지 않는 ID")
	@Test
	void getProduct_fail_whenProductNotFound() {
	    Long invalidId = 9999L;

	    assertThatThrownBy(() -> productService.getProductById(invalidId))
	        .isInstanceOf(NotFoundException.class)
	        .hasMessageContaining("해당 상품을 찾을 수 없습니다");
	}
}
