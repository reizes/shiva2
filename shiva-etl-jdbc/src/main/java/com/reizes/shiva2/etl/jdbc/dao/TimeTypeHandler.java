package com.reizes.shiva2.etl.jdbc.dao;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Date 타입 핸들러 (java.util.Date, java.sql.Date, java.sql.Time, java.sql.Timestamp 모두 적용)
 * @author inho
 * @since 2009-12-10
 */
public class TimeTypeHandler implements TypeHandler<Time, Time> {

	public Time getDBTypeValue(Object value) throws Exception {
		return (Time)value;
	}

	public Time getJavaTypeValue(Object value) throws Exception {
		if (value instanceof Date) {
			return new Time(((Date)value).getTime());
		}

		if (value instanceof java.sql.Date) {
			return new Time(((java.sql.Date)value).getTime());
		}

		if (value instanceof Time) {
			return (Time)value;
		}

		if (value instanceof Timestamp) {
			return new Time(((Timestamp)value).getTime());
		}

		return null;
	}

}
