package com.reizes.shiva2.jdbc.loader;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.reizes.shiva2.utils.StringUtil;

public class MapLoader extends AbstractJDBCLoader {
	private boolean useCamel = false;
	private boolean lowerCase = true;

	public MapLoader() {
		super();
	}

	public MapLoader(DataSource datasource) {
		super(datasource);
	}

	public MapLoader(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public MapLoader(Properties prop) throws Exception {
		super(prop);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getData(Object object, String name) {
		Map<String, Object> map = (Map<String, Object>)object;
		
		String key = lowerCase?name.toLowerCase():name;
		
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

	public boolean isLowerCase() {
		return lowerCase;
	}

	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}

}
