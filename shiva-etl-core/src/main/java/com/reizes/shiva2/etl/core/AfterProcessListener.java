package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface AfterProcessListener {
	public void onAfterProcess(ProcessContext context,Object output);
}
