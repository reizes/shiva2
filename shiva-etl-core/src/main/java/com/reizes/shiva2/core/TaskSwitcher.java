package com.reizes.shiva2.core;

import java.util.List;

public interface TaskSwitcher {
	public Tasks switchTask(Object input);
	public List<Tasks> getAllTasks();
}
