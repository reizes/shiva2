package com.reizes.shiva2.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collection Utilities
 * @author reizes
 * @since 2009.10.14
 * @since 2010.6.7 change Object to Generic
 * @since 0.2.0
 */
public class CollectionUtil {
	/**
	 * object array를 Set&lt;Object&gt;로 변환하여 반환
	 * @param array Input Array
	 * @return - Set&lt;Object&gt;
	 */
	public static <T> Set<T> toSet(T[] array) {
		HashSet<T> set = new HashSet<T>();

		if (array != null) {
			for (T object : array) {
				set.add(object);
			}
		}

		return set;
	}

	/**
	 * same toSet but this will remove duplicated value
	 * 2012.10.31
	 * @since 2.1.5
	 * @param array Input Array
	 * @return Set&lt;Object&gt;
	 */
	public static <T> Set<T> toUniqueSet(T[] array) {
		HashSet<T> set = new HashSet<T>();

		if (array != null) {
			for (T object : array) {
				if (!set.contains(object)) {
					set.add(object);
				}
			}
		}

		return set;
	}

	/**
	 * Change arrays of key and value to Map
	 */
	public static <K, V> Map<K, V> toMap(K[] keys, V[] values) {
		HashMap<K, V> map = new HashMap<K, V>();

		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], values != null ? (i < values.length ? values[i] : null) : null);
			}
		}

		return map;
	}

	/**
	 * Union two collections and return Set
	 * @param set1 Collection1
	 * @param set2 Collection2
	 * @return Union Set&lt;Object&gt;
	 */
	public static <T> Set<T> union(Collection<T> set1, Collection<T> set2) {
		HashSet<T> set = new HashSet<T>();

		for (T object : set1) {
			set.add(object);
		}
		for (T object : set2) {
			if (!set.contains(object)) {
				set.add(object);
			}
		}

		return set;
	}

	/**
	 * Merge src to dest
	 * @param dest Set&lt;Object&gt;
	 * @param src Collection
	 * @return dest merged with src
	 */
	public static <T> Set<T> unionTo(Set<T> dest, Collection<T> src) {
		for (T object : src) {
			if (!dest.contains(object)) {
				dest.add(object);
			}
		}

		return dest;
	}

	/**
	 * return intersection of two collections
	 * @param set1 Collection1
	 * @param set2 Collection2
	 * @return - intersection as Set&lt;Object&gt;
	 */
	public static <T> Set<T> intersect(Collection<T> set1, Collection<T> set2) {
		HashSet<T> set = new HashSet<T>();

		for (T object : set1) {
			if (set2.contains(object)) {
				set.add(object);
			}
		}

		return set;
	}

}
