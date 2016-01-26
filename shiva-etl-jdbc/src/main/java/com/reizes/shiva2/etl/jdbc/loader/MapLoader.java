package com.reizes.shiva2.etl.jdbc.loader;

import java.util.Map;

import com.reizes.shiva2.utils.StringUtil;

public class MapLoader extends AbstractJDBCLoader {
	private boolean useCamel = false;

	@SuppressWarnings("unchecked")
	@Override
	protected Object getData(Object object, String name) {
		Map<String, Object> map = (Map<String, Object>)object;
		String key = name.toLowerCase();
		
		if (useCamel) {
			key = StringUtil.camelize(key);
		}
		
		return map.get(key);
	}

	public boolean isUseCamel() {
		return useCamel;
	}

	public void setUseCamel(boolean useCamel) {
		this.useCamel = useCamel;
	}

}
