package org.kosa.tripTalk.product;

import org.kosa.tripTalk.common.dto.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProductRepositoryCustom {
	Page<ProductResponseDTO> searchAll(Pageable pageable, Search search);
	Optional<Product> findByIdWithSellerAndCategory(Long id);
}
