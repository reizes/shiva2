package com.reizes.shiva2.etl.jdbc.loader;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * 입력된 값 하나만 사용하는 value loader
 * @author reizes
 * @since 2010.8.3
 * @since 2.1.0
 */
public class ValueLoader extends AbstractJDBCLoader {
	public ValueLoader() {
		super();
	}

	public ValueLoader(DataSource datasource) {
		super(datasource);
	}

	public ValueLoader(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public ValueLoader(Properties prop) throws Exception {
		super(prop);
	}

	@Override
	protected Object getData(Object object, String name) {
		return object;
	}

}
