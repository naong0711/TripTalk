package org.kosa.tripTalk.common.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;

public class PredicateBuilder {
	
	public static BooleanExpression applyKeyword(StringPath field, String keyword) {
		return field.containsIgnoreCase(keyword);
	}
}
