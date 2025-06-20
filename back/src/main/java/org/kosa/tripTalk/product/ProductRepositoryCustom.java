package org.kosa.tripTalk.product;

import org.kosa.tripTalk.common.dto.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
	Page<ProductResponseDTO> searchAll(Pageable pageable, Search search);
}
