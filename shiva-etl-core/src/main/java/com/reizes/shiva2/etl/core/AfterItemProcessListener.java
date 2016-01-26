package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface AfterItemProcessListener {
	public void onAfterItemProcess(ProcessContext context,Object data);
}
