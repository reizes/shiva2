package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface BeforeItemProcessAware {
	public void onBeforeItemProcess(ProcessContext context,Object data) throws Exception;
}
