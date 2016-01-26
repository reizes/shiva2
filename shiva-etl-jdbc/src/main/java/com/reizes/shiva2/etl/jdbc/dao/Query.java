package com.reizes.shiva2.etl.jdbc.dao;

import java.util.List;

/**
 * JDBC 쿼리와 관련 쿼리 관련 파라미터 리스트를 갖는 객체
 * @author inho
 * @since 2009-09-11
 */
class Query {
	private final String query;
	private final List<Object> parameters;

	Query(String query, List<Object> parameters) {
		this.query = query;
		this.parameters = parameters;
	}

	String getQuery() {
		return query;
	}

	List<Object> getParameters() {
		return parameters;
	}
}
