package org.kosa.tripTalk.common.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.StringPath;

public class PredicateBuilder {
	
	// like 검색 (대소문자 구별x)
	public static BooleanExpression applyKeyword(StringPath field, String keyword) {
		return field.containsIgnoreCase(keyword);
	}
	
	// between 범위 검색
	// 범위안, 이상, 이하 조건
	public static <T extends Comparable<?>> BooleanExpression between(ComparableExpression<T> field, T start, T end) {
		if (start != null && end != null) return field.between(start, end); 
	    if (start != null) return field.goe(start);
	    if (end != null) return field.loe(end);
		
		return null;
	}
}
