package org.kosa.tripTalk.product;

import java.util.List;

import org.kosa.tripTalk.common.dto.Search;
import org.kosa.tripTalk.common.querydsl.QuerydslUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
	
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ProductResponseDTO> searchAll(Pageable pageable, Search search) {
		QProduct product = QProduct.product;
		
		BooleanExpression condition = ProductPredicateBuilder.build(product, search);
		
		List<Product> contents = queryFactory
							.selectFrom(product)
							.where(condition)
							.offset(pageable.getOffset())
							.limit(pageable.getPageSize())
							.orderBy(QuerydslUtils.toOrders(pageable.getSort(), product))
							.fetch();
		
		Long total = queryFactory
							.select(product.count())
							.from(product)
							.where(condition)
							.fetchOne();
		
		List<ProductResponseDTO> dtoList = contents.stream()
						.map(ProductResponseDTO::from)
						.toList();
		
		return new PageImpl<>(dtoList, pageable, total);
	}
}
