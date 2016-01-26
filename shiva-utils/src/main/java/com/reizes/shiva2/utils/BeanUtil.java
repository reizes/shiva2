package com.reizes.shiva2.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Bean 관련 유틸리티 모음
 * @author reizes
 * @since 2009.9.23
 * @since 2010.1.26
 */
public class BeanUtil {
	/**
	 * convert하지 않는 클래스는 이 인터페이스를 상속한다.
	 * @author reizes
	 * @since 2010.1.26
	 */
	public interface NotConvertClass {
	}

	/**
	 * Number로 입력된 데이터를 지정 타입으로 캐스팅 한다.
	 * 2010.1.26 apache의 ConvertUtils를 사용하는 것으로 변경
	 * @param input - Number
	 * @param targetType - Class<?> 캐스팅할 타입
	 * @return - Object
	 */
	public static Object numberCast(Object input, Class<?> targetType) {
		return ConvertUtils.convert(input, targetType);
	}

	/**
	 * 2010.1.26
	 * Map<String, Object> data로부터 bean을 세팅한다. data의 key는 _로 구분되어 있을 경우 camelize한다.
	 * @param bean - 세팅할 bean
	 * @param data - Map
	 * @throws IllegalAccessException -
	 * @throws InvocationTargetException -
	 * @throws NoSuchMethodException -
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
	 * obj를 class 로 변경시켜 프로퍼티를 복사한다.
	 * @param <T> - 리턴할 클래스 
	 * @param cls - 리턴할 클래스
	 * @param obj - 변경할 개체
	 * @return - T로 지정된 클래스
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
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

	/**
	 * String->Date의 경우 별도의 Date.parse를 사용하고 그렇지 않은 경우 apache의 convert를 사용한다.
	 * @param value
	 * @param targetType
	 * @return
	 * @since 2.1.5
	 */
	public static Object convert(Object value, Class<?> targetType) {
		if (Date.class.equals(targetType) && value.getClass().equals(String.class)) { // String을 Date로 컨버트 하는 경우
			return DateUtil.parse((String)value);
		} else {
			return ConvertUtils.convert(value, targetType);
		}
	}
}
