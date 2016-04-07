package com.reizes.shiva2.jdbc.dao;

/**
 * 타입 핸들러 인터페이스
 * @author inho
 * @param <T1> 자바 리턴 타입
 * @param <T2> DB 리턴 타입
 * @since 2009-12-02
 */
public interface TypeHandler<T1, T2> {
	/**
	 * @param value 타입 객체
	 * @return 자바 타입 객체
	 * @throws Exception -
	 */
	public T1 getJavaTypeValue(Object value) throws Exception;

	/**
	 * @param value 타입 객체
	 * @return DB 타입 객체
	 * @throws Exception -
	 */
	public T2 getDBTypeValue(Object value) throws Exception;
}
