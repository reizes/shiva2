package com.reizes.shiva2.etl.core.transformer;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.reizes.shiva2.utils.StringUtil;

/**
 * @author reizes
 * @since 2010.10.19
 */
public class ModelToMySqlDumpTransformer extends AbstractTransformer {
	private String[] columns;

	@Override
	public Object doProcess(Object input) throws Exception {
		String[] output = new String[columns.length];

		for (int i = 0; i < columns.length; i++) {
			String name = StringUtil.camelize(columns[i]);

			if (PropertyUtils.isReadable(input, name)) {
				Object data = PropertyUtils.getSimpleProperty(input, name);

				if (data != null) {
					if (data instanceof Date) {
						output[i] = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", data);
					} else if (data instanceof Boolean) {
						output[i] = (Boolean)data ? "1" : "0";
					} else {
						output[i] = "\"" + StringUtils.replaceEach(data.toString().trim(),
							new String[] {"\"", "\\", "\t"},
							new String[] {"\\\"", "\\\\", "\\t"}) + "\"";
					}
				} else {
					output[i] = "NULL";
				}
			}
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
