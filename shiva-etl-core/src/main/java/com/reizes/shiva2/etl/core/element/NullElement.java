package com.reizes.shiva2.etl.core.element;

import com.reizes.shiva2.etl.core.EtlElement;

public class NullElement implements EtlElement {

	@Override
	public Object doProcess(Object input) throws Exception {
		return input;
	}
}
