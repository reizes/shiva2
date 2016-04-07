package com.reizes.shiva2.core.task;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Map<String,Object> to TSV Transformer
 * @author reizes
 * @since 2009.9.17 - outputTitle 추가
 * @since 2.1.0 - delimiter 지정 추가
 */
public class MapToTSV extends AbstractTask {
	String[] columns;
	private boolean outputTitle = false;
	private int lineNum = 0;
	private String delimiter = "\t";

	/**
	 * Map<String,Object> to TSV Transformer
	 * @param input - Map<String,Object> 
	 * @return - String TSV
	 * @throws Exception -
	 * @see com.reizes.shiva2.core.Task#doProcess(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		Map<String, Object> map = (Map<String, Object>)input;
		StringBuilder sb = new StringBuilder();

		if (lineNum == 0 && outputTitle) {
			// output column title
			sb.append(StringUtils.join(columns, delimiter)).append('\n');
		}

		for (int i = 0; i < columns.length; i++) {
			Object obj = map.get(columns[i]);

			if (obj != null) {
				sb.append(obj.toString().trim());
			}
			if (i < columns.length - 1) {
				sb.append(delimiter);
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
