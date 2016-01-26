package com.reizes.shiva2.etl.jdbc.loader;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetLoader extends AbstractJDBCLoader {

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
