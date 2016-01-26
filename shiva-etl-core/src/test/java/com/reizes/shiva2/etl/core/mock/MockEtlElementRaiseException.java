package com.reizes.shiva2.etl.core.mock;

import com.reizes.shiva2.etl.core.EtlElement;

public class MockEtlElementRaiseException implements EtlElement {
	private static int processCount=0;
	private static int stop=0;
	private Exception exception;
	
	private static synchronized void increaseProcessCount() {
		MockEtlElementRaiseException.processCount++;
	}
	
	public static synchronized void resetProcessCount() {
		MockEtlElementRaiseException.processCount=0;
	}
	
	public static synchronized int getProcessCount() {
		return MockEtlElementRaiseException.processCount;
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		Long data=(Long)input;
		if (data==MockEtlElementRaiseException.stop) {
			throw exception;
		}
		//System.out.println(MockEtlElementRaiseException.getProcessCount()+" processing");
		MockEtlElementRaiseException.increaseProcessCount();
		return data;
	}

	public static int getStop() {
		return MockEtlElementRaiseException.stop;
	}

	public static void setStop(int stop) {
		MockEtlElementRaiseException.stop = stop;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
