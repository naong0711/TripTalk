package org.kosa.tripTalk.common.querydsl;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;

public class QuerydslUtils {
	public static OrderSpecifier<?>[] toOrders(Sort sort, EntityPath<?> root) {
		return sort.stream()
				.map(order -> {
					PathBuilder pathBuilder = new PathBuilder<>(root.getType(), root.getMetadata());
					return order.isAscending()
							? pathBuilder.getString(order.getProperty()).asc()
							: pathBuilder.getString(order.getProperty()).desc();
				})
				.toArray(OrderSpecifier[]::new);
	}
}
