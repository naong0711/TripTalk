package org.kosa.tripTalk.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.seller.Seller;
import org.kosa.tripTalk.seller.SellerRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
	@DisplayName("상품 목록 조회")
	void getAllProducts_success() {
	    // given
	    productRepository.save(Product.builder()
	            .title("제주도 여행")
	            .description("3박 4일 힐링")
	            .address("제주")
	            .price(300000)
	            .startDate(LocalDateTime.now().plusDays(3))
	            .endDate(LocalDateTime.now().plusDays(6))
	            .category(savedCategory)
	            .seller(savedSeller)
	            .build());

	    productRepository.save(Product.builder()
	            .title("부산 투어")
	            .description("2박 3일 먹방")
	            .address("부산")
	            .price(250000)
	            .startDate(LocalDateTime.now().plusDays(2))
	            .endDate(LocalDateTime.now().plusDays(4))
	            .category(savedCategory)
	            .seller(savedSeller)
	            .build());

	    // when
	    List<ProductResponseDTO> result = productService.getAllProducts();

	    // then
	    assertThat(result).hasSize(2);
	    assertThat(result).extracting("title")
	        .containsExactlyInAnyOrder("제주도 여행", "부산 투어");
	}
	
	
}
