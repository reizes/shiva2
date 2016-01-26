package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface BeforeProcessAware {
	public void onBeforeProcess(ProcessContext context,Object data) throws Exception;
}
