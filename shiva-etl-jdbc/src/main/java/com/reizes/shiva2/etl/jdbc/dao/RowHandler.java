package com.reizes.shiva2.etl.jdbc.dao;

/**
 * 스트림 모드 핸들러 인터페이스
 * @author inho
 * @since 2009-09-11
 */
public interface RowHandler {
	/**
	 * @param row resultClass로 지정한 객체
	 * @throws Exception -
	 */
	void handle(Object row) throws Exception;
}
