package com.reizes.shiva2.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>$replacement$ 패턴과 #value# 패턴을 JDBC 쿼리로 변환한다.</pre>
 * @author inho
 * @since 2009-09-11
 */
public class QueryBuilder {
	private static final Pattern STRING_PATTERN = Pattern.compile("\\$([^\\$]+)\\$");
	private static final Pattern VALUE_PATTERN = Pattern.compile("#([^#]+)#");

	private Map<Class<?>, TypeHandler<?, ?>> handlerMap;

	public QueryBuilder() {
	}

	public QueryBuilder(Map<Class<?>, TypeHandler<?, ?>> handlerMap) {
		this.handlerMap = handlerMap;
	}

	public void setHandlerMap(Map<Class<?>, TypeHandler<?, ?>> handlerMap) {
		this.handlerMap = handlerMap;
	}

	/**
	 * @param query SQL 문
	 * @param param SQL 파라미터
	 * @return Query 객체 (JDBC 쿼리와 파리미터 리스트를 관리)
	 * @throws Exception -
	 */
	public Query build(String query, Object param) throws Exception {
		if (StringUtils.isBlank(query)) {
			throw new IllegalArgumentException("query is blank.");
		}

		return replaceValuePattern(replaceStringPattern(query, param), param);
	}

	private String replaceStringPattern(String query, Object param) throws Exception {
		if (param == null) {
			return query;
		}

		StringBuffer result = new StringBuffer();
		Matcher matcher = STRING_PATTERN.matcher(query);

		while (matcher.find()) {
			Object value = getParamValue(matcher.group(1), param);
			
			if (value == null) {
				throw new IllegalArgumentException(matcher.group());
			}
			
			matcher.appendReplacement(result, String.valueOf(value));
		}

		matcher.appendTail(result);
		return result.toString();
	}

	private Object getParamValue(String keys, Object param) throws Exception {
		int indexOfDot = keys.indexOf(".");

		if (indexOfDot != -1) {
			String key = keys.substring(0, indexOfDot);
			String nextKey = keys.substring(indexOfDot + 1, keys.length());

			return getParamValue(nextKey, getParamPropertyValue(key, param));
		}

		return getParamPropertyValue(keys, param);
	}

	private Object getParamPropertyValue(String key, Object param) throws Exception {
		if (param == null) {
			return null;
		}
		
		if (param instanceof Map<?, ?>) {
			return ((Map<?, ?>)param).get(key);
		}

		if (PropertyUtils.isReadable(param, key)) {
			return getTypeHandlerValue(PropertyUtils.getSimpleProperty(param, key));
		}

		return getTypeHandlerValue(param);
	}

	private Object getTypeHandlerValue(Object param) throws Exception {
		if (param == null) {
			return null;
		}

		TypeHandler<?, ?> handler = handlerMap.get(param.getClass());

		if (handler != null) {
			return handler.getDBTypeValue(param);
		}

		return ConvertUtils.convert(param);
	}

	private Query replaceValuePattern(String query, Object param) throws Exception {
		if (param == null) {
			return new Query(query, null);
		}

		StringBuffer resultQuery = new StringBuffer();
		List<Object> resultParam = new ArrayList<Object>();

		Matcher matcher = VALUE_PATTERN.matcher(query);

		while (matcher.find()) {
			matcher.appendReplacement(resultQuery, "?");
			resultParam.add(getParamValue(matcher.group(1), param));
		}

		matcher.appendTail(resultQuery);
		return new Query(resultQuery.toString(), resultParam);
	}
}
