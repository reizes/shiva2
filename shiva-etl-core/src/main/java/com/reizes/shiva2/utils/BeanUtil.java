package com.reizes.shiva2.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Utility Functions for Bean
 * @author reizes
 * @since 2009.9.23
 * @since 2010.1.26
 */
public class BeanUtil {
	/**
	 * Should implement this interface to not convert
	 * @author reizes
	 * @since 2010.1.26
	 */
	public interface NotConvertClass {
	}

	/**
	 * Cast Number to input type
	 * 2010.1.26 using apache common ConvertUtils
	 * @param input Number
	 * @param targetType Class&lt;?&gt; Target type to cast
	 * @return Object
	 */
	public static Object numberCast(Object input, Class<?> targetType) {
		return ConvertUtils.convert(input, targetType);
	}

	/**
	 * 2010.1.26
	 * Set properties of input Bean using Map&lt;String, Object&gt;. If the key of map is snaked, change it using camel convention.
	 * @param bean Bean object to set
	 * @param data Data Map
	 */
	public static void setFromMap(Object bean, Map<String, ?> data) throws IllegalAccessException,
		InvocationTargetException,
		NoSuchMethodException {
		for (String key : data.keySet()) {
			Object value = data.get(key);
			String name = StringUtil.camelize(key);

			if (PropertyUtils.isWriteable(bean, name)) {
				Class<?> cls = PropertyUtils.getPropertyType(bean, name);

				if (!NotConvertClass.class.isAssignableFrom(cls)) {
					Object converted = ConvertUtils.convert(value, PropertyUtils.getPropertyType(bean, name));
					PropertyUtils.setSimpleProperty(bean, name, converted);
				}
			}
		}
	}

	/**
	 * Copy properties of input Object using input Class.
	 * @param cls Class to return
	 * @param obj input Object
	 * @return Class what has type cls
	 */
	public static Object changeClass(Class<?> cls, Object obj) throws InstantiationException,
		IllegalAccessException,
		InvocationTargetException,
		NoSuchMethodException {
		Object retObj = cls.newInstance();
		Field[] fields = obj.getClass().getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();

			if (PropertyUtils.isReadable(obj, fieldName) && PropertyUtils.isWriteable(retObj, fieldName)) {
				PropertyUtils.setSimpleProperty(retObj, fieldName, PropertyUtils.getSimpleProperty(obj, fieldName));
			}
		}

		return retObj;
	}

	public static Object convert(Object value, Class<?> targetType) {
		if (Date.class.equals(targetType) && value.getClass().equals(String.class)) { // String을 Date로 컨버트 하는 경우
			return DateUtil.parse((String)value);
		} else {
			return ConvertUtils.convert(value, targetType);
		}
	}
}
