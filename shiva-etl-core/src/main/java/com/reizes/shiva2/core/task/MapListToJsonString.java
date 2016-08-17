package com.reizes.shiva2.core.task;

import java.util.List;
import java.util.Map;

import com.reizes.shiva2.utils.JsonUtils;

public class MapListToJsonString extends AbstractTask {
	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> mapList = (List<Map<String, Object>>) input;
		for (Map<String, Object> map : mapList) {
			sb.append(JsonUtils.toJson(map)).append("\n");
		}
		return sb.toString();
	}

}
