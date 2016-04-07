package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;

public interface ExceptionListener {
	public void onException(ProcessContext context,Object input,Exception e);
}
