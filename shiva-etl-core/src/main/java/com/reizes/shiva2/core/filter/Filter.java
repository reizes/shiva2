package com.reizes.shiva2.core.filter;

import com.reizes.shiva2.core.Task;

public interface Filter extends Task {
	public boolean isFiltered(Object input);
}
