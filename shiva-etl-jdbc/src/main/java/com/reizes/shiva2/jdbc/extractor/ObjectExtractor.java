package com.reizes.shiva2.jdbc.extractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.utils.StringUtil;

/*
 * Extract ResultSet To Object 
 * 첫 번째 컬럼을 Object로 반환
 */
public class ObjectExtractor extends ResultSetExtractor {
	private boolean useCamel = false;

	@Override
	protected Object fetchResultSet(ResultSet rs, Object input) throws Exception {
		if (rs == null) {
			throw new InvalidPropertyException("ResultSet is null");
		}
		
		while (rs.next()) {
			input = startProcessItem(rs.getObject(1));
			executeInvalidateQuery();
		}
		
		return input;
	}

	public boolean isUseCamel() {
		return useCamel;
	}

	public void setUseCamel(boolean useCamel) {
		this.useCamel = useCamel;
	}

}
