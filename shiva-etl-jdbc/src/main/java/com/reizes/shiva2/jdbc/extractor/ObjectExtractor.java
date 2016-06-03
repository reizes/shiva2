package com.reizes.shiva2.jdbc.extractor;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.reizes.shiva2.core.InvalidPropertyException;

/*
 * Extract ResultSet To Object 
 * 첫 번째 컬럼을 Object로 반환
 */
public class ObjectExtractor extends ResultSetExtractor {
	private boolean useCamel = false;

	public ObjectExtractor() {
		super();
	}

	public ObjectExtractor(DataSource datasource) {
		super(datasource);
	}

	public ObjectExtractor(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public ObjectExtractor(Properties prop) throws Exception {
		super(prop);
	}

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
