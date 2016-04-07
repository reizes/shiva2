package com.reizes.shiva2.core.filter;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public abstract class AbstractFilter implements Filter, ProcessContextAware {
	ProcessContext context;
	
	@Override
	public final Object doProcess(Object input) throws Exception {
		if (isFiltered(input)) {
			this.context.setExecutionStatus(ExecutionStatus.SKIP);
			return null;
		}
		
		return input;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}

}
