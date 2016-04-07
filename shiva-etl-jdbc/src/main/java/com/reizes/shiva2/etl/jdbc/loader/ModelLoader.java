package com.reizes.shiva2.etl.jdbc.loader;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;

import com.reizes.shiva2.type.EnumType;
import com.reizes.shiva2.utils.StringUtil;

public class ModelLoader extends AbstractJDBCLoader {
	private boolean useCamel = true;

	public ModelLoader() {
		super();
	}

	public ModelLoader(DataSource datasource) {
		super(datasource);
	}

	public ModelLoader(Map<String, Object> datasourceProperties) throws Exception {
		super(datasourceProperties);
	}

	public ModelLoader(Properties prop) throws Exception {
		super(prop);
	}

	@Override
	protected Object getData(Object object, String name) throws Exception {
		if (useCamel) {
			name = StringUtil.camelize(name);
		}
		if (PropertyUtils.isReadable(object, name)) {
			Object property = PropertyUtils.getSimpleProperty(object, name);
			if (property instanceof Enum || property instanceof EnumType) {
				return property.toString();
			}
			return property;
		}

		return null;
	}

	public boolean isUseCamel() {
		return useCamel;
	}

	public void setUseCamel(boolean useCamel) {
		this.useCamel = useCamel;
	}

}
