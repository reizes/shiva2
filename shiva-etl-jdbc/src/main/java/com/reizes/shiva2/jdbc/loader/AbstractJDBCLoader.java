package com.reizes.shiva2.jdbc.loader;

import java.io.Flushable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.loader.AbstractLoader;

public abstract class AbstractJDBCLoader extends AbstractLoader implements Flushable, BeforeProcessAware, AfterProcessAware {
	private DataSource datasource;
	private String query;
	private boolean supportsBatchUpdates;
	private int batchUpdateSize = 1000;
	private boolean enableBatchUpdates = true;

	/*
	 * internal properties
	 */
	private Connection connection;
	private Pattern valuePattern = Pattern.compile("#([^\\#]+)#", Pattern.MULTILINE);
	private Pattern replacePattern = Pattern.compile("\\$([^\\$]+)\\$", Pattern.MULTILINE);
	private LinkedList<String> parameterList;
	private String processedQuery;
	private PreparedStatement preparedStatement;
	private boolean doReplace = false; // $column$가 포함되었는지 여부
	private int curBatchUpdateCnt = 0;

	abstract protected Object getData(Object object, String name) throws Exception;

	public AbstractJDBCLoader() {
		
	}
	
	public AbstractJDBCLoader(DataSource datasource) {
		this.setDatasource(datasource);
	}
	
	public AbstractJDBCLoader(Properties prop) throws Exception {
		this.setDatasource(prop);
	}
	
	public AbstractJDBCLoader(Map<String, Object> datasourceProperties) throws Exception {
		this.setDatasource(datasourceProperties);
	}
	/*
	 * prepare parameter value : #column# -> ?
	 */
	private void prepareQuery() {
		parameterList = new LinkedList<String>();
		Matcher matcher = valuePattern.matcher(query);
		
		while (matcher.find()) {
			parameterList.add(matcher.group(1));
		}
		
		processedQuery = matcher.replaceAll("?");

		matcher.reset(processedQuery);
		matcher.usePattern(replacePattern);
		doReplace = matcher.find();
	}

	/*
	 * process query replace : $column$ -> value
	 */
	private String processQueryReplace(Object object) throws Exception {
		StringBuilder sb = new StringBuilder(processedQuery);
		Matcher matcher = replacePattern.matcher(processedQuery);
		int offset = 0;
		
		while (matcher.find()) {
			String name = matcher.group(1);
			String value = getData(object, name).toString();
			sb.replace(matcher.start() + offset, matcher.end() + offset, value);
			offset += value.length() - (name.length() + 2);
		}
		
		return sb.toString();
	}

	private void setParameter(Object object, PreparedStatement stmt) throws Exception {
		int parameterIndex = 1;
		
		for (String name : parameterList) {
			stmt.setObject(parameterIndex, getData(object, name));
			parameterIndex++;
		}
	}
	
	private void connect() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connection = datasource.getConnection();
			
			if (isEnableBatchUpdates()) {
				DatabaseMetaData dbMetaData = connection.getMetaData();
				supportsBatchUpdates = dbMetaData.supportsBatchUpdates();
				if (supportsBatchUpdates) {
					connection.setAutoCommit(false);
				}
			}
			if (!doReplace) {
				try {
					preparedStatement = connection.prepareStatement(processedQuery);
				} catch (SQLException e) {
					connection.close();
					connection = null;
					throw e;
				}
			}
		}
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		if (query == null) {
			throw new InvalidPropertyException("query is null");
		}
		if (datasource == null) {
			throw new InvalidPropertyException("datasource is null");
		}
		
		prepareQuery();
		connect();
		
		if (isEnableBatchUpdates()) {
			DatabaseMetaData dbMetaData = connection.getMetaData();
			supportsBatchUpdates = dbMetaData.supportsBatchUpdates();
		}
		if (!doReplace) {
			try {
				preparedStatement = connection.prepareStatement(processedQuery);
			} catch (SQLException e) {
				connection.close();
				connection = null;
				throw e;
			}
		}
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws SQLException {
		if (isSupportsBatchUpdates() && curBatchUpdateCnt > 0) {
			connect();
			preparedStatement.executeBatch();
			curBatchUpdateCnt = 0;
		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (connection != null) {
			connection.close();
		}
		
		preparedStatement = null;
		connection = null;
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		connect();
		if (doReplace) {
			preparedStatement = connection.prepareStatement(processQueryReplace(input));
			preparedStatement.clearParameters();
			setParameter(input, preparedStatement);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} else {
			setParameter(input, preparedStatement);
			
			if (isSupportsBatchUpdates()) {
				preparedStatement.addBatch();
				curBatchUpdateCnt++;
				
				if (curBatchUpdateCnt == getBatchUpdateSize()) {
					preparedStatement.executeBatch();
					curBatchUpdateCnt = 0;
					connection.commit();
				}
			} else {
				preparedStatement.executeUpdate();
			}
		}
		
		return input;
	}
	
	@Override
	public void flush() throws IOException  {
		if (isSupportsBatchUpdates()) {
			try {
				connect();
				preparedStatement.executeBatch();
				connection.commit();
				curBatchUpdateCnt = 0;
			} catch (SQLException e) {
				throw new IOException(e);
			}
		}
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isSupportsBatchUpdates() {
		return supportsBatchUpdates;
	}

	public int getBatchUpdateSize() {
		return batchUpdateSize;
	}

	public void setBatchUpadteSize(int batchUpdateSize) {
		this.batchUpdateSize = batchUpdateSize;
	}

	public boolean isEnableBatchUpdates() {
		return enableBatchUpdates;
	}

	public void setEnableBatchUpdates(boolean enableBatchUpdates) {
		this.enableBatchUpdates = enableBatchUpdates;
	}

}
