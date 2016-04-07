package com.reizes.shiva2.core.task;

import com.reizes.shiva2.core.Task;

public class NoOp implements Task {

	@Override
	public Object doProcess(Object input) throws Exception {
		return input;
	}
}
