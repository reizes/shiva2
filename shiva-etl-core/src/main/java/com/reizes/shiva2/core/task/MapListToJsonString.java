package com.reizes.shiva2.core.task;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class MapListToJsonString extends AbstractTask {
	private Gson gson = new Gson();

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> mapList = (List<Map<String, Object>>) input;
		for (Map<String, Object> map : mapList) {
			sb.append(gson.toJson(map)).append("\n");
		}
		return sb.toString();
	}

}
