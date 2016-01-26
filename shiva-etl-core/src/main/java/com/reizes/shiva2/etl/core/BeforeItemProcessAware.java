package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface BeforeItemProcessAware {
	public void onBeforeItemProcess(ProcessContext context,Object data) throws Exception;
}
