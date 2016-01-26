package com.reizes.shiva2.etl.core;

public interface EtlElement {
	public Object doProcess(Object input) throws Exception;
}
