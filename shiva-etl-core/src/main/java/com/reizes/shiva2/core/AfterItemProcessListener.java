package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface AfterItemProcessListener {
	public void onAfterItemProcess(ProcessContext context,Object data);
}
