package com.reizes.shiva2.jdbc.dao;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Date 타입 핸들러 (java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp 모두 적용)
 * @author inho
 * @since 2009-12-10
 */
public class TimestampTypeHandler implements TypeHandler<Timestamp, Timestamp> {

	public Timestamp getDBTypeValue(Object value) throws Exception {
		return (Timestamp)value;
	}

	public Timestamp getJavaTypeValue(Object value) throws Exception {
		if (value instanceof Date) {
			return new Timestamp(((Date)value).getTime());
		}

		if (value instanceof java.sql.Date) {
			return new Timestamp(((java.sql.Date)value).getTime());
		}

		if (value instanceof Time) {
			return new Timestamp(((Time)value).getTime());
		}

		if (value instanceof Timestamp) {
			return (Timestamp)value;
		}

		return null;
	}

}
