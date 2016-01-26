package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface AfterProcessAware {
	public void onAfterProcess(ProcessContext context,Object data) throws Exception;
}
