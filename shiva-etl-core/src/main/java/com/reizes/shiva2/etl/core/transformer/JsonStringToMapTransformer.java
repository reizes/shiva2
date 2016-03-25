package com.reizes.shiva2.etl.core.transformer;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class JsonStringToMapTransformer extends AbstractTransformer {
	private Gson gson = new Gson();

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		Map<String, Object> resultMap = gson.fromJson((String)input, HashMap.class);
		return resultMap;
	}

}
