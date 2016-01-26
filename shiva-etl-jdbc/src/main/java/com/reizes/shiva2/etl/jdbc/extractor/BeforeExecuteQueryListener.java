package com.reizes.shiva2.etl.jdbc.extractor;

import java.util.Map;

public interface BeforeExecuteQueryListener {
	public void onBeforeExecuteQuery(long executeCount);
	public Map<String, Object> getQueryParameterValues();
}
