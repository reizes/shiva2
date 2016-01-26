package com.reizes.shiva2.etl.jdbc.loader;


/**
 * 입력된 값 하나만 사용하는 value loader
 * @author reizes
 * @since 2010.8.3
 * @since 2.1.0
 */
public class ValueLoader extends AbstractJDBCLoader {
	@Override
	protected Object getData(Object object, String name) {
		return object;
	}

}
