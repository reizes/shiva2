package com.reizes.shiva2.jdbc.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

public class ResultSetLoader extends AbstractJDBCLoader {

	public ResultSetLoader() {
		super();
	}

	public ResultSetLoader(DataSource datasource) {
		super(datasource);
	}

	public ResultSetLoader(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public ResultSetLoader(Properties prop) throws Exception {
		super(prop);
	}

	@Override
	protected Object getData(Object object, String name) {
		ResultSet rs=(ResultSet)object;
		Object ret=null;
		try {
			ret = rs.getObject(name);
		} catch (SQLException e) {
			ret=null;
		}
		return ret;
	}

}
