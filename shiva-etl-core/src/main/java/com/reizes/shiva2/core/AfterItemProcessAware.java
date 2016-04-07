package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface AfterItemProcessAware {
	public void onAfterItemProcess(ProcessContext context,Object data) throws Exception;
}
