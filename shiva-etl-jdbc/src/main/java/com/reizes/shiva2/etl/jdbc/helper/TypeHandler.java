package com.reizes.shiva2.etl.jdbc.helper;

/**
 * jdbc type handler 인터페이스
 * @author reizes
 * @since 2009.10.9
 */
public interface TypeHandler {
	/**
	 * value에서 DB type 반환
	 * @param value - enum
	 * @return - db
	 */
	public Object userTypeToDb(Object value);

	/**
	 * db type에서 value 반환
	 * @param data
	 * @param dbValue
	 */
	public Object dbToUserType(Object dbValue);
}
