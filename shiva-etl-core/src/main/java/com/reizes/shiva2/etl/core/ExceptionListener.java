package com.reizes.shiva2.etl.core;

import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface ExceptionListener {
	public void onException(ProcessContext context,Object input,Exception e);
}
