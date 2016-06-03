package com.reizes.shiva2.jdbc.extractor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.extractor.AbstractExtractor;

/**
 * ResultSetExractor 상위 클래스 생성
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 * @since 2016.6.3 - 0.2.1
 */
public abstract class AbstractResultSetExtractor extends AbstractExtractor {
	private DataSource datasource;
	private Connection conn = null;

	// 2012.8.7 invalidate query - 내부적으로 지정 카운트당 invalidate query를 수행한다.
	private String invalidateQuery = "SELECT 1";
	private long invalidateCount = 5000; // 5000개 마다 invalidate
	private long currentCount = 0;
	private long processedRowCount = 0;

	public AbstractResultSetExtractor() {
		
	}
	
	public AbstractResultSetExtractor(DataSource datasource) {
		this.setDatasource(datasource);
	}
	
	public AbstractResultSetExtractor(Properties prop) throws Exception {
		this.setDatasource(prop);
	}
	
	public AbstractResultSetExtractor(Map<String, Object> datasourceProperties) throws Exception {
		this.setDatasource(datasourceProperties);
	}

	protected abstract ResultSet getResultSet(Connection conn) throws Exception;

	protected abstract void beforeCloseResultSet() throws Exception;
	
	protected Object execute(Object input) throws Exception {
		ResultSet rs = null;
		Object output = input;
		processedRowCount = 0;

		if (datasource == null) {
			throw new InvalidPropertyException("datasource is null");
		}
		try {
			conn = datasource.getConnection();
			rs = getResultSet(conn);
			rs.setFetchSize(1);
			output = fetchResultSet(rs, output);
		} finally {
			beforeCloseResultSet();

			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				conn.close();
			}

			rs = null;
			conn = null;
		}

		return output;
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		Object output = input;
		currentCount = 0;
		output = execute(input);

		return output;
	}

	protected Object fetchResultSet(ResultSet rs, Object input) throws Exception {
		while (rs.next()) {
			input = startProcessItem(rs);
			executeInvalidateQuery();
		}
		return input;
	}

	/**
	 * 2012.8.7 invalidate query를 실행한다. - fetchResultSet 내에서 startProcessItem 호출 후 호출되어야 한다.
	 * @throws SQLException -
	 */
	protected void executeInvalidateQuery() throws SQLException {
		if (conn != null) {
			currentCount++;

			if (currentCount >= invalidateCount) {
				currentCount = 0;
				Statement stmt = conn.createStatement();
				stmt.execute(invalidateQuery);
				stmt.close();
			}
		}
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public AbstractResultSetExtractor setDatasource(DataSource datasource) {
		this.datasource = datasource;
		return this;
	}

	public void setDatasource(Properties prop) throws Exception {
		this.datasource = BasicDataSourceFactory.createDataSource(prop);
	}

	public void setDatasource(Map<String, Object> datasourceProperties) throws Exception {
		Properties prop = new Properties();
		prop.putAll(datasourceProperties);
		this.datasource = BasicDataSourceFactory.createDataSource(prop);
	}

	public String getInvalidateQuery() {
		return invalidateQuery;
	}

	public AbstractResultSetExtractor setInvalidateQuery(String invalidateQuery) {
		this.invalidateQuery = invalidateQuery;
		return this;
	}

	public long getInvalidateCount() {
		return invalidateCount;
	}

	public AbstractResultSetExtractor setInvalidateCount(long invalidateCount) {
		this.invalidateCount = invalidateCount;
		return this;
	}

	public long getProcessedRowCount() {
		return processedRowCount;
	}

	@Override
	protected Object startProcessItem(Object item) throws Exception {
		Object result = super.startProcessItem(item);
		processedRowCount++;
		return result;
	}

}
