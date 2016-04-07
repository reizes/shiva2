package com.reizes.shiva2.jdbc.task;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import com.reizes.shiva2.core.InvalidPropertyException;

public class QueryToMap extends QueryToResultSet {
	
	@Override
	protected Object fetchResultSet(ResultSet rs,Object input) throws Exception {
		LinkedList<Map<String,Object>> list=new LinkedList<Map<String,Object>>();

		if (rs==null) throw new InvalidPropertyException("ResultSet is null");
		ResultSetMetaData metaData=rs.getMetaData();
		int colCnt=metaData.getColumnCount();
		while(rs.next()) {
			WeakHashMap<String,Object> map=new WeakHashMap<String,Object>();
			for(int index=1;index<=colCnt;index++) {
				map.put(metaData.getColumnName(index).toLowerCase(), rs.getObject(index));
			}
			list.add(map);
		}
		return list;
	}

}
