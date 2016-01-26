package com.reizes.shiva2.etl.jdbc.transformer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;

import org.apache.commons.beanutils.PropertyUtils;

import com.reizes.shiva2.etl.core.InvalidPropertyException;

public class QueryToModelTransformer extends QueryToResultSetTransformer {
	private Class<?> modelClass;
	
	@Override
	protected Object fetchResultSet(ResultSet rs,Object input) throws Exception {
		LinkedList<Object> list=new LinkedList<Object>();

		if (rs==null) throw new InvalidPropertyException("ResultSet is null");
		if (modelClass==null) throw new InvalidPropertyException("ModelClass is null");
		ResultSetMetaData metaData=rs.getMetaData();
		int colCnt=metaData.getColumnCount();
		while(rs.next()) {
			Object model=modelClass.newInstance();
			for(int index=1;index<=colCnt;index++) {
				String colName=metaData.getColumnName(index).toLowerCase(); // 컬럼 이름은 소문자로 간주
				if (PropertyUtils.isWriteable(model, colName)) {
					PropertyUtils.setSimpleProperty(model, colName, rs.getObject(index));
				}
			}
			list.add(modelClass.cast(model));
		}
		return list;
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<?> modelClass) {
		this.modelClass = modelClass;
	}

}
