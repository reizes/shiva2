package com.reizes.shiva2.etl.core.transformer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Model To TSV Transformer
 * @author reizes
 * @since 2009.9.17
 * @since 2.1.0 - delimiter 추가
 */
public class ModelToTSVTransformer extends AbstractTransformer {
	private String[] columns;
	private boolean outputTitle = false;
	private int lineNum = 0;
	private String delimiter = "\t";

	/**
	 * Model To TSV Transformer
	 * @param input - Object
	 * @return - String
	 * @throws Exception -
	 * @see com.reizes.shiva2.etl.core.EtlElement#doProcess(java.lang.Object)
	 */
	@Override
	public Object doProcess(Object input) throws Exception {
		StringBuilder sb = new StringBuilder();

		if (lineNum == 0) {
			// output column title
			sb.append(StringUtils.join(columns, delimiter)).append('\n');
		}

		for (int i = 0; i < columns.length; i++) {
			String name = columns[i];

			if (PropertyUtils.isReadable(input, name)) {
				Object obj = PropertyUtils.getSimpleProperty(input, name);

				if (obj != null) {
					sb.append(obj.toString().trim());
				}
				if (i < columns.length - 1) {
					sb.append(delimiter);
				}
			}
		}

		sb.append('\n');
		lineNum++;
		return sb.toString();
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public boolean isOutputTitle() {
		return outputTitle;
	}

	public void setOutputTitle(boolean outputTitle) {
		this.outputTitle = outputTitle;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
