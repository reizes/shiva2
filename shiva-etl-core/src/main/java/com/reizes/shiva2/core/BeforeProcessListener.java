package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface BeforeProcessListener {
	public void onBeforeProcess(ProcessContext context,Object input);
}
