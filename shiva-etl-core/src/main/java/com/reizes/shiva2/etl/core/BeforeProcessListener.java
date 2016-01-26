package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface BeforeProcessListener {
	public void onBeforeProcess(ProcessContext context,Object input);
}
