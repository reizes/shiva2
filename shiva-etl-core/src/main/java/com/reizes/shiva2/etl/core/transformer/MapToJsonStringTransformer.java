package com.reizes.shiva2.etl.core.transformer;

import com.google.gson.Gson;

public class MapToJsonStringTransformer extends AbstractTransformer {
	private Gson gson = new Gson();

	@Override
	public Object doProcess(Object input) throws Exception {
		return gson.toJson(input);
	}

}
