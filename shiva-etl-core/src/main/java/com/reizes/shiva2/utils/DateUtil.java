package com.reizes.shiva2.utils;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

/**
 * @author reizes
 * @since 0.2.0
 */
public class DateUtil {
	public static Date min(Date d1, Date d2) {
		if (d1 == null) {
			return d2;
		} else if (d2 == null) {
			return d1;
		}

		return d1.compareTo(d2) < 0 ? d1 : d2;
	}

	public static Date max(Date d1, Date d2) {
		if (d1 == null) {
			return d2;
		} else if (d2 == null) {
			return d1;
		}

		return d1.compareTo(d2) < 0 ? d2 : d1;
	}

	/**
	 * parse the string using typical datetime format
	 * @param data date string
	 * @return Date Date object
	 * @since 0.2.0
	 */
	public static Date parse(String data) {
		try {
			return DateUtils.parseDate(data, new String[] {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss"});
		} catch (ParseException e) {
			return null;
		}
	}
}
