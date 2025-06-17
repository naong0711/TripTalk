package org.kosa.tripTalk.product;

import org.kosa.tripTalk.category.Category;
import org.kosa.tripTalk.category.CategoryRepository;
import org.kosa.tripTalk.seller.Seller;
import org.kosa.tripTalk.seller.SellerRepository;
import org.kosa.tripTalk.user.User;
import org.kosa.tripTalk.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final SellerRepository sellerRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public void create(ProductRequest request) {
		 // 1. 판매자 유저 조회
		User sellerUser = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자 유저가 존재하지 않습니다."));
		
		// 2. 판매자 프로필 조회
        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자 프로필이 존재하지 않습니다."));
        
     // 3. 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
        
     // 4. 상품 생성 및 저장
        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .seller(seller)
                .category(category)
                .build();
        
        productRepository.save(product);
	}
}
