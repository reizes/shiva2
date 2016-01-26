package com.reizes.shiva2.etl.core.transformer;

import java.util.HashMap;
import java.util.Map;

/**
 * Map To Map Transformer
 * @author reizes
 * @since 2.1.1
 */
public class MapToMapTransformer extends AbstractTransformer {
	private Map<String, String> fieldNameMap; // old - new field name map

	/**
	 * @param input
	 * @return
	 * @throws Exception
	 * @see com.reizes.shiva2.etl.core.EtlElement#doProcess(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		Map<String, ?> map = (Map<String, ?>)input;
		Map<String, Object> newMap = new HashMap<String, Object>();

		for (String key : map.keySet()) {
			String newKey = fieldNameMap.get(key);

			if (newKey != null) {
				newMap.put(newKey, map.get(key));
			}
		}

		return newMap;
	}

	public Map<String, String> getFieldNameMap() {
		return fieldNameMap;
	}

	public void setFieldNameMap(Map<String, String> fieldNameMap) {
		this.fieldNameMap = fieldNameMap;
	}

	public void addFieldNameFair(String oldName, String newName) {
		if (fieldNameMap == null) {
			fieldNameMap = new HashMap<String, String>();
		}

		fieldNameMap.put(oldName, newName);
	}
}
