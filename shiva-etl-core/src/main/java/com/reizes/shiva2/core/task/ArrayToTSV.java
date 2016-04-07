package com.reizes.shiva2.core.task;

/**
 * Array to TSV Transformer
 * @author reizes
 * @since 2012.9.26
 * @since 2.1.5
 */
public class ArrayToTSV extends AbstractTask {
	private String delimiter = "\t";

	/**
	 * Map<String,Object> to TSV Transformer
	 * @param input - Map<String,Object> 
	 * @return - String TSV
	 * @throws Exception -
	 * @see com.reizes.shiva2.core.Task#doProcess(java.lang.Object)
	 */
	@Override
	public Object doProcess(Object input) throws Exception {
		Object[] arr = (Object[])input;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delimiter);
			}
			if (arr[i] != null) {
				sb.append(arr[i].toString().trim());
			}
		}

		sb.append('\n');
		return sb.toString();
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
