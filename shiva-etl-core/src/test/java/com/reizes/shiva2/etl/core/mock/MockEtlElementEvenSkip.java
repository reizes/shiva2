package com.reizes.shiva2.etl.core.mock;

import com.reizes.shiva2.etl.core.EtlElement;
import com.reizes.shiva2.etl.core.ExecutionStatus;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.context.ProcessContextAware;

public class MockEtlElementEvenSkip implements EtlElement,ProcessContextAware {
	private static int processCount=0;
	private int min=0;
	private ProcessContext context;
	
	private static synchronized void increaseProcessCount() {
		MockEtlElementEvenSkip.processCount++;
	}
	
	public static synchronized void resetProcessCount() {
		MockEtlElementEvenSkip.processCount=0;
	}
	
	public static synchronized int getProcessCount() {
		return MockEtlElementEvenSkip.processCount;
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		Long data=(Long)input;
		if (data>=min && data%2==0) {
			this.context.setExecutionStatus(ExecutionStatus.SKIP);
			return data;
		}
		//System.out.println(MockEtlElementEvenSkip.getProcessCount()+" processing");
		MockEtlElementEvenSkip.increaseProcessCount();
		return data;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context=context;
	}

}
