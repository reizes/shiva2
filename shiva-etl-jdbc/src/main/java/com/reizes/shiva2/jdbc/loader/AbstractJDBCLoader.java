package com.reizes.shiva2.jdbc.loader;

import java.io.Flushable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;

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
	private long sleepTimeWhenDeadlock = 1000;
	private boolean enableBatchUpdates = true;

	/*
	 * internal properties
	 */
	private Connection connection;
	private Pattern valuePattern = Pattern.compile("#([^\\#]+)#", Pattern.MULTILINE);
	private Pattern replacePattern = Pattern.compile("\\$([^\\$]+)\\$", Pattern.MULTILINE);
	private List<String> parameterList;
	private String processedQuery;
	private PreparedStatement preparedStatement;
	private boolean doReplace = false; // $column$가 포함되었는지 여부
	private AtomicInteger curBatchUpdateCnt = new AtomicInteger(0);
	private long updatedCount = 0;
	
	// for retry when lock wait timeout
	private List<Object> parameterObjectList;

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
		parameterList = new ArrayList<String>();
		parameterObjectList = new ArrayList<Object>();
		
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

	private void setParameter(Object object, PreparedStatement stmt) throws SQLException {
		int parameterIndex = 1;
		
		for (String name : parameterList) {
			try {
				stmt.setObject(parameterIndex, getData(object, name));
			} catch (Exception e) {
				stmt.setObject(parameterIndex, null);
				e.printStackTrace();
			}
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
	}
	
	private synchronized int executeBatch(PreparedStatement preparedStatement) throws SQLException {
		connect();
		do {
			try {
				int[] results = preparedStatement.executeBatch();
				connection.commit();
				clearBatch(preparedStatement);
				
				for(int i=0;i<results.length;i++) {
					if (results[i]==PreparedStatement.EXECUTE_FAILED) {
						System.out.println(i+"'th batch is failed..");
					}
				}
				
				return results.length;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				if (StringUtils.indexOf(e.getMessage(), "try restarting transaction")>=0) {
					System.out.println("retrying....");
					reprepareBatch(preparedStatement);
					try {
						Thread.sleep(sleepTimeWhenDeadlock);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} else {
					throw e;
				}
			}
		} while(true);
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws SQLException {
		if (isSupportsBatchUpdates() && curBatchUpdateCnt.get() > 0) {
			executeBatch(preparedStatement);
			curBatchUpdateCnt.set(0);
		}
		if (preparedStatement != null) {
			preparedStatement.clearBatch();
			preparedStatement.close();
		}
		if (connection != null) {
			connection.close();
		}
		
		preparedStatement = null;
		connection = null;
	}
	
	private void addBatch(PreparedStatement preparedStatement, Object object) throws SQLException {
		parameterObjectList.add(object);
		preparedStatement.addBatch();
		preparedStatement.clearParameters();
	}
	
	private void clearBatch(PreparedStatement preparedStatement) throws SQLException {
		parameterObjectList.clear();
		preparedStatement.clearBatch();
	}
	
	private void reprepareBatch(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.clearBatch();
		for (Object object : parameterObjectList) {
			setParameter(object, preparedStatement);
			preparedStatement.addBatch();
			preparedStatement.clearParameters();
		}
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		connect();
		updatedCount = 0;
		if (doReplace) {
			preparedStatement = connection.prepareStatement(processQueryReplace(input));
			preparedStatement.clearParameters();
			setParameter(input, preparedStatement);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} else {
			setParameter(input, preparedStatement);
			
			if (isSupportsBatchUpdates()) {
				addBatch(preparedStatement, input);
				int curBatchCount = curBatchUpdateCnt.incrementAndGet();
				
				if (curBatchCount == getBatchUpdateSize()) {
					updatedCount = executeBatch(preparedStatement);
					curBatchUpdateCnt.set(0);
				}
			} else {
				updatedCount = preparedStatement.executeUpdate();
				preparedStatement.clearParameters();
			}
		}
		
		return input;
	}
	
	@Override
	public void flush() throws IOException  {
		if (isSupportsBatchUpdates()) {
			try {
				updatedCount = executeBatch(preparedStatement);
				curBatchUpdateCnt.set(0);
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

	public long getUpdatedCount() {
		return updatedCount;
	}
}
