package org.kosa.tripTalk.product;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.common.dto.Search;
import org.kosa.tripTalk.exception.NotFoundException;
import org.kosa.tripTalk.seller.Seller;
import org.kosa.tripTalk.seller.SellerRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // ✅ 상품 생성
    @Transactional
    public Long create(ProductRequestDTO request) {
        try {
            System.out.println("판매자 ID: " + request.getSellerId());
            Seller sellerUser = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new NotFoundException("판매자 유저가 존재하지 않습니다."));
            System.out.println("판매자 유저 조회 완료");

            Seller seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new NotFoundException("판매자 프로필이 존재하지 않습니다."));
            System.out.println("판매자 프로필 조회 완료");

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다."));
            System.out.println("카테고리 조회 완료");

            Product product = request.toEntity(seller, category);
            System.out.println("Product 엔티티 생성 완료: " + product);

            productRepository.save(product);
            System.out.println("Product 저장 완료, ID: " + product.getId());

            return product.getId();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // ✅ 상품 상세 조회
    public ProductResponseDTO getProductById(Long id) {
        System.out.println("++++++++++++++getProductById+++++++++++++" + id);
        Product product = productRepository.findByIdWithDiscount(id).orElse(null);
        return ProductResponseDTO.from(product);
    }

    // ✅ 상품 수정
    public Product update(Long id, ProductRequestDTO dto) {
        Product product = notFoundProductId(id);
        product.updateFromDTO(dto);
        return product;
    }

    // ✅ 전체 상품 조회
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable, Search search) {
        return productRepository.searchAll(pageable, search);
    }

    // ✅ 상품 검색
    public List<Product> search(String location, LocalDate checkIn, LocalDate checkOut, int people) {
        return productRepository.findAllByLocationAndDateAndPeople(location, checkIn, checkOut, people);
    }

    // ✅ 상품 저장 (직접 엔티티 전달)
    public Long save(Product product) {
        Product saved = productRepository.save(product);
        return saved.getId();
    }

    // ✅ 지역 목록 (프론트에서 select 동적 생성용)
    public List<String> getAvailableLocations() {
        return productRepository.findDistinctLocations();
    }

    // ✅ 예외 처리용 헬퍼
    private Product notFoundProductId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 상품을 찾을 수 없습니다"));
    }
}
