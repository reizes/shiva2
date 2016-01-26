package com.reizes.shiva2.etl.core.transformer;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.reizes.shiva2.etl.core.InvalidPropertyException;
import com.reizes.shiva2.utils.BeanUtil;
import com.reizes.shiva2.utils.StringUtil;

/**
 * map을 지정된 Model로 변환하는 Transformer
 * @author reizes
 * @since 2012.5.30
 * @since 2.1.1
 */
public class MapToModelTransformer extends AbstractTransformer {
	private Class<?> modelClass;
	private boolean useCamel = true;

	/**
	 * map을 지정된 Model로 변환하는 Transformer
	 * @param input - Element
	 * @return - Model
	 * @throws Exception -
	 * @see com.reizes.shiva2.etl.core.EtlElement#doProcess(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		if (modelClass == null) {
			throw new InvalidPropertyException("modelClass is null!");
		}

		Map<String, ?> map = (Map<String, ?>)input;

		Object model = this.modelClass.newInstance();

		for (String name : map.keySet()) {
			Object value = map.get(name);

			if (useCamel) {
				name = StringUtil.camelize(name);
			}
			try {
				if (PropertyUtils.isWriteable(model, name)) {
					PropertyUtils.setSimpleProperty(model, name, BeanUtil.convert(value, PropertyUtils.getPropertyType(model, name)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		map.clear();

		return model;
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<?> modelClass) {
		this.modelClass = modelClass;
	}

	public boolean isUseCamel() {
		return useCamel;
	}

	public void setUseCamel(boolean useCamel) {
		this.useCamel = useCamel;
	}

}
