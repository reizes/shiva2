package com.reizes.shiva2.etl.core.mock;

import com.reizes.shiva2.core.Task;

public class MockEtlElement implements Task {
	private static int processCount=0;
	
	private static synchronized void increaseProcessCount() {
		MockEtlElement.processCount++;
	}
	
	public static synchronized void resetProcessCount() {
		MockEtlElement.processCount=0;
	}
	
	public static synchronized int getProcessCount() {
		return MockEtlElement.processCount;
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		//System.out.println(MockEtlElement.getProcessCount()+" processing");
		MockEtlElement.increaseProcessCount();
		return input;
	}

}
