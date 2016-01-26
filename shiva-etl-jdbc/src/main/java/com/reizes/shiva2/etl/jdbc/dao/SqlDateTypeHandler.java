package com.reizes.shiva2.etl.jdbc.dao;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;

/**
 * java.sql.Date 타입 핸들러 (java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp 모두 적용)
 * @author inho
 * @since 2009-12-10
 */
public class SqlDateTypeHandler implements TypeHandler<Date, Date> {

	public Date getDBTypeValue(Object value) throws Exception {
		return (Date)value;
	}

	public Date getJavaTypeValue(Object value) throws Exception {
		if (value instanceof Date) {
			return (Date)value;
		}

		if (value instanceof java.util.Date) {
			return new Date(((java.util.Date)value).getTime());
		}

		if (value instanceof Time) {
			return new Date(((Time)value).getTime());
		}

		if (value instanceof Timestamp) {
			return new Date(((Timestamp)value).getTime());
		}

		return null;
	}

}
