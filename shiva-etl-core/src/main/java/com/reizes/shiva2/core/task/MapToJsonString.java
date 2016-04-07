package com.reizes.shiva2.core.task;

import com.google.gson.Gson;

public class MapToJsonString extends AbstractTask {
	private Gson gson = new Gson();

	@Override
	public Object doProcess(Object input) throws Exception {
		return gson.toJson(input);
	}

}
