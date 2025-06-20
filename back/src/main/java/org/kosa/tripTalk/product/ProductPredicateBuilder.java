package org.kosa.tripTalk.product;

import org.kosa.tripTalk.common.dto.Search;
import org.kosa.tripTalk.common.predicate.PredicateBuilder;

import com.querydsl.core.types.dsl.BooleanExpression;

public class ProductPredicateBuilder {
	
	public static BooleanExpression build(QProduct product, Search search) {
		if (search == null || !search.isValid())
			return null;
		
		String key = search.getSearchKey();
		String value = search.getSearchValue();
		
		return switch (key) {
				case "title" -> PredicateBuilder.applyKeyword(product.title, value);
				case "description" -> PredicateBuilder.applyKeyword(product.description, value);
				case "address" -> PredicateBuilder.applyKeyword(product.address, value);
				default -> null;
		};
	}

}
