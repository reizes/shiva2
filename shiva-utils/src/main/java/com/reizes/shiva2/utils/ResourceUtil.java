package com.reizes.shiva2.utils;

import java.io.InputStream;
import java.net.URL;

/**
 * ClassLoader를 이용하여 Resource를 리턴하는 유틸
 * @author inho
 * @since 2010-01-11
 */
public class ResourceUtil {
	/**
	 * @param name Resource Name
	 * @return Resource URL
	 */
	public static URL getResource(String name) {
		ClassLoader cl = getClassLoader();

		if (cl == null) {
			return null;
		}

		return cl.getResource(name);
	}

	/**
	 * @param name Resource Name
	 * @return Resource Input Stream
	 */
	public static InputStream getResourceAsStream(String name) {
		ClassLoader cl = getClassLoader();

		if (cl == null) {
			return null;
		}

		return cl.getResourceAsStream(name);
	}

	private static ClassLoader getClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		if (cl == null) {
			return ClassLoader.getSystemClassLoader();
		}

		return cl;
	}
}
