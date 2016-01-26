package com.reizes.shiva2.etl.core.transformer;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Model To Object[] Transformer
 * 지정한 properties의 순서대로 object[]로 만든다.
 * 2010.11.18
 * @author reizes
 * @since 2.1.0
 */
public class ModelToObjectArrayTransformer extends AbstractTransformer {
	private String[] properties;

	/**
	 * Model To TSV Transformer
	 * @param input - Object
	 * @return - String
	 * @throws Exception -
	 * @see com.reizes.shiva2.etl.core.EtlElement#doProcess(java.lang.Object)
	 */
	@Override
	public Object doProcess(Object input) throws Exception {
		Object[] data = new Object[properties.length];

		for (int i = 0; i < properties.length; i++) {
			String name = properties[i];

			if (PropertyUtils.isReadable(input, name)) {
				data[i] = PropertyUtils.getSimpleProperty(input, name);
			}
		}

		return data;
	}

	public String[] getProperteis() {
		return properties;
	}

	public void setProperteis(String[] properties) {
		this.properties = properties;
	}

}
