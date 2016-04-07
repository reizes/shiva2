package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface AfterProcessListener {
	public void onAfterProcess(ProcessContext context,Object output);
}
