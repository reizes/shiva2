package com.reizes.shiva2.jdbc.extractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.utils.StringUtil;

/*
 * Extract ResultSet To Map
 */
public class MapExtractor extends ResultSetExtractor {
	private boolean useCamel = false;

	@Override
	protected Object fetchResultSet(ResultSet rs, Object input) throws Exception {
		if (rs == null) {
			throw new InvalidPropertyException("ResultSet is null");
		}

		ResultSetMetaData metaData = rs.getMetaData();
		int colCnt = metaData.getColumnCount();

		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();

			for (int index = 1; index <= colCnt; index++) {
				String name = metaData.getColumnLabel(index).toLowerCase();

				if (useCamel) {
					name = StringUtil.camelize(name);
				}

				map.put(name, rs.getObject(index));
			}

			input = startProcessItem(map);
			//executeInvalidateQuery();	// 2012.9.4 오류가 발생해서 일단 막음
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
