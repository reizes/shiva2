package com.reizes.shiva2.core.task;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.reizes.shiva2.utils.StringUtil;

/**
 * @author reizes
 * @since 2010.10.19
 */
public class MapToMySqlDump extends AbstractTask {
	private String[] columns;

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		Map<String, Object> map = (Map<String, Object>)input;
		String[] output = new String[columns.length];

		for (int i = 0; i < columns.length; i++) {
			Object data = map.get(columns[i]);
			output[i] = data != null ? ("\""
				+ StringUtils.replaceEach(data.toString().trim(), new String[] {"\"", "\\", "\t"}, new String[] {
					"\\\"", "\\\\", "\\t"}) + "\"") : "NULL";
		}

		String str = StringUtil.join(output, '\t') + '\n';
		return str;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

}
