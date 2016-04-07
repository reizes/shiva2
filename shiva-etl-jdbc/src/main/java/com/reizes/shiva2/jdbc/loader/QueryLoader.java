package com.reizes.shiva2.jdbc.loader;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.loader.AbstractLoader;

public class QueryLoader extends AbstractLoader implements BeforeProcessAware, AfterProcessAware {
	private DataSource datasource;
	private Connection connection;

	public QueryLoader() {
		
	}
	
	public QueryLoader(DataSource datasource) {
		this.setDatasource(datasource);
	}
	
	public QueryLoader(Properties prop) throws Exception {
		this.setDatasource(prop);
	}
	
	public QueryLoader(Map<String, Object> datasourceProperties) throws Exception {
		this.setDatasource(datasourceProperties);
	}

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

	public void setDatasource(Properties prop) throws Exception {
		this.datasource = BasicDataSourceFactory.createDataSource(prop);
	}

	public void setDatasource(Map<String, Object> datasourceProperties) throws Exception {
		Properties prop = new Properties();
		prop.putAll(datasourceProperties);
		this.datasource = BasicDataSourceFactory.createDataSource(prop);
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
