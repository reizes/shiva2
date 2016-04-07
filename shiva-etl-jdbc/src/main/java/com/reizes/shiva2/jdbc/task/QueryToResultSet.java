package com.reizes.shiva2.jdbc.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import javax.sql.DataSource;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.Task;

public class QueryToResultSet implements Task {
	private DataSource datasource;

	@Override
	public Object doProcess(Object input) throws Exception {
		String query=(String)input;
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		Object output=input;
		Exception ex=null;
		if (query==null || query.length()==0) return null;
		if (datasource==null) throw new InvalidPropertyException("datasource is null");
		try {
			conn=datasource.getConnection();
			stmt=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			//stmt.setFetchSize(Integer.MIN_VALUE);
			rs=stmt.executeQuery(query);
			//rs.setFetchSize(1);
			output=fetchResultSet(rs,output);
		} catch(Exception e) {
			ex=e;
		} finally {
			if (rs!=null) rs.close();
			if (stmt!=null) stmt.close();
			if (conn!=null) conn.close();
			rs=null;
			stmt=null;
			conn=null;
		}
		if (ex!=null) throw ex;
		return output;
	}
	
	protected Object fetchResultSet(ResultSet rs,Object input) throws Exception {
		LinkedList<ResultSet> list=new LinkedList<ResultSet>();
		while(rs.next()) {
			list.add(rs);
		}
		return list;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

}
