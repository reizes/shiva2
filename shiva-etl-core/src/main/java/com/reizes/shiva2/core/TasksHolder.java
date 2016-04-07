package com.reizes.shiva2.core;

import java.util.List;

public interface TasksHolder {
	public List<Task> getTasks();
	public TasksHolder setTasks(List<Task> elementList);
	public TasksHolder setTask(Task element);
	public TasksHolder addTask(Task element);
	public TasksHolder addTasks(List<Task>  elementList);
}
