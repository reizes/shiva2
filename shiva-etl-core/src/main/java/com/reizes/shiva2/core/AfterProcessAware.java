package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface AfterProcessAware {
	public void onAfterProcess(ProcessContext context,Object data) throws Exception;
}
