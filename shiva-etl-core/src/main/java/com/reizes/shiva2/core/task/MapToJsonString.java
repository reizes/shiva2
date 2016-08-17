package com.reizes.shiva2.core.task;

import com.reizes.shiva2.utils.JsonUtils;

public class MapToJsonString extends AbstractTask {
	@Override
	public Object doProcess(Object input) throws Exception {
		return JsonUtils.toJson(input);
	}

}
