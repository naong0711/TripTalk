package org.kosa.tripTalk.product;

import java.util.List;
import java.util.Optional;

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

    // 페이징, 검색, 정렬 리스트
    @Override
    public Page<ProductResponseDTO> searchAll(Pageable pageable, Search search) {
        QProduct product = QProduct.product;
        BooleanExpression condition = ProductPredicateBuilder.build(product, search);
        
        List<Product> contents = fetchContent(pageable, product, condition);
        Long total = fetchCount(product, condition);
        List<ProductResponseDTO> dtoList = toDtoList(contents);
        
        return new PageImpl<>(dtoList, pageable, total);
    }

    // dto변환
    private List<ProductResponseDTO> toDtoList(List<Product> contents) {
        return contents.stream()
                        .map(ProductResponseDTO::from)
                        .toList();
    }

    // 카운트
    private Long fetchCount(QProduct product, BooleanExpression condition) {
        return queryFactory
                        .select(product.count())
                        .from(product)
                        .where(condition)
                        .fetchOne();
    }

    // 내용 가져오기
    private List<Product> fetchContent(Pageable pageable, QProduct product, BooleanExpression condition) {
        return queryFactory
                        .selectFrom(product)
                        .where(condition)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(QuerydslUtils.toOrders(pageable.getSort(), product))
                        .fetch();
    }

    // 특정 ID로 연관관계 fetch join 하여 단건 조회
    @Override
    public Optional<Product> findByIdWithSellerAndCategory(Long id) {
        QProduct product = QProduct.product;
        return Optional.ofNullable(queryFactory.selectFrom(product)
                .leftJoin(product.seller).fetchJoin()
                .leftJoin(product.seller.user).fetchJoin()
                .leftJoin(product.category).fetchJoin()
                .where(product.id.eq(id))
                .fetchOne());
    }
}