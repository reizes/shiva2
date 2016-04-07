package com.reizes.shiva2.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collection 관련 유용한 유틸리티들
 * @author reizes
 * @since 2009.10.14
 * @since 2010.6.7 Object -> Generic으로 변경
 * @since 2.1.0 -> toMap method 추가
 */
public class CollectionUtil {
	/**
	 * object array를 Set<Object>로 변환하여 반환
	 * @param array - 입력 배열
	 * @return - Set<Object>
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
	 * toSet과 같으나 중복 요소는 없도록 한다.
	 * 2012.10.31
	 * @since 2.1.5
	 * @param array
	 * @return
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
	 * key, value의 array를 map으로 변경
	 * @param <K>
	 * @param <V>
	 * @param keys
	 * @param values
	 * @return
	 * @since 2.1.0
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
	 * 두 Collection을 union 하여 set으로 반환
	 * @param set1 - Collection
	 * @param set2 - Collection
	 * @return - Union된 Set<Object>
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
	 * dest와 src를 union하여 dest를 반환
	 * @param dest - Set<Object>
	 * @param src - Collection
	 * @return - src와 union된 dest
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
	 * 두 Collection을 intersect 하여 set으로 반환
	 * @param set1 - Collection
	 * @param set2 - Collection
	 * @return - intersect된 Set<Object>
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
