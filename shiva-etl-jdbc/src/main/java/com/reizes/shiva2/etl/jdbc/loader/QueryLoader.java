package com.reizes.shiva2.etl.jdbc.loader;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import com.reizes.shiva2.etl.core.AfterProcessAware;
import com.reizes.shiva2.etl.core.BeforeProcessAware;
import com.reizes.shiva2.etl.core.InvalidPropertyException;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.loader.AbstractLoader;

public class QueryLoader extends AbstractLoader implements BeforeProcessAware, AfterProcessAware {
	private DataSource datasource;
	private Connection connection;

	@Override
	public Object doProcess(Object input) throws Exception {
		String query = (String)input;
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
		stmt = null;
		return input;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		if (datasource == null) {
			throw new InvalidPropertyException("datasource is null");
		}
		
		connection = datasource.getConnection();
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		if (connection != null) {
			connection.close();
		}
		
		connection = null;
	}

}
