package com.reizes.shiva2.jdbc.extractor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.text.StrSubstitutor;

/**
 * 기존 Extractor를 AbstractResultSetExtractor 상속으로 변경
 * @author reizes
 * @since 2.1.0
 * @since 2010.4.12
 */
public class ResultSetExtractor extends AbstractResultSetExtractor {
	private String query;
	private String executableQuery;
	private Statement stmt = null;
	// 2016.1.26 select 쿼리를 반복하며 파라메터를 바꿀 수 있도록 기능 추가 
	private boolean repeatQueryUntilEmpty = false;	// 쿼리 반복 수행 (결과가 없을 때 까지)
	private Map<String, Object> queryParameter; // String Substitutor를 위한 parameter
	private long executeCount = 0;
	private String parameterPrefix = "${";
	private String parameterSuffix = "}";
	private BeforeExecuteQueryListener beforeExecuteQueryListener;

	public ResultSetExtractor() {
		super();
	}

	public ResultSetExtractor(DataSource datasource) {
		super(datasource);
	}

	public ResultSetExtractor(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public ResultSetExtractor(Properties prop) throws Exception {
		super(prop);
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		Object output = input;
		
		do {
			if (repeatQueryUntilEmpty) {
				if (beforeExecuteQueryListener != null) {
					beforeExecuteQueryListener.onBeforeExecuteQuery(executeCount);
					queryParameter = beforeExecuteQueryListener.getQueryParameterValues();
				}
				if (queryParameter!=null) {
					StrSubstitutor sub = new StrSubstitutor(queryParameter);
					sub.setVariablePrefix(parameterPrefix);
					sub.setVariableSuffix(parameterSuffix);
					executableQuery = sub.replace(this.query);
				}
			}
			output = super.execute(input);
			executeCount++;
		} while(repeatQueryUntilEmpty && super.getProcessedRowCount()>0);
		
		return output;
	}
	
	@Override
	protected ResultSet getResultSet(Connection conn) throws SQLException {
		stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		try {
			stmt.setFetchSize(Integer.MIN_VALUE);
		} catch (Exception e) {
			stmt.setFetchSize(1);
		}
		
		return stmt.executeQuery(executableQuery); 
	}

	@Override
	protected void beforeCloseResultSet() throws SQLException {
		if (stmt != null) {
			stmt.close();
		}

		stmt = null;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
		this.executableQuery = query;
	}

	public boolean isRepeatQueryUntilEmpty() {
		return repeatQueryUntilEmpty;
	}

	public void setRepeatQueryUntilEmpty(boolean repeatQueryUntilEmpty) {
		this.repeatQueryUntilEmpty = repeatQueryUntilEmpty;
	}

	public Map<String, Object> getQueryParameter() {
		return queryParameter;
	}

	public void setQueryParameter(Map<String, Object> queryParameter) {
		this.queryParameter = queryParameter;
	}

	public String getParameterPrefix() {
		return parameterPrefix;
	}

	public void setParameterPrefix(String parameterPrefix) {
		this.parameterPrefix = parameterPrefix;
	}

	public String getParameterSuffix() {
		return parameterSuffix;
	}

	public void setParameterSuffix(String parameterSuffix) {
		this.parameterSuffix = parameterSuffix;
	}

	public BeforeExecuteQueryListener getBeforeExecuteQueryListener() {
		return beforeExecuteQueryListener;
	}

	public void setBeforeExecuteQueryListener(BeforeExecuteQueryListener beforeExecuteQueryListener) {
		this.beforeExecuteQueryListener = beforeExecuteQueryListener;
	}

	public long getExecuteCount() {
		return executeCount;
	}

}
