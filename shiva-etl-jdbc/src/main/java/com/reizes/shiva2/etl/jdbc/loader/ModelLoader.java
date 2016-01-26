package com.reizes.shiva2.etl.jdbc.loader;

import org.apache.commons.beanutils.PropertyUtils;

import com.reizes.shiva2.type.EnumType;
import com.reizes.shiva2.utils.StringUtil;

public class ModelLoader extends AbstractJDBCLoader {
	private boolean useCamel = true;

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
