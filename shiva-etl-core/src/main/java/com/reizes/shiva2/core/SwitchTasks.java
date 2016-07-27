package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class SwitchTasks implements Task, TasksProcessorAware, ProcessContextAware, BeforeProcessAware, AfterProcessAware, BeforeItemProcessAware, AfterItemProcessAware {
	private TaskSwitcher switcher;
	
	public SwitchTasks(TaskSwitcher switcher) {
		this.switcher = switcher;
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		if (switcher!=null) {
			Tasks tasks = switcher.switchTask(input);
			return tasks!=null?tasks.doProcess(input):null;
		}
		
		return input;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		if (switcher != null) {
			for(Tasks tasks : switcher.getAllTasks()) {
				tasks.setProcessContext(context);
			}
		}
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		if (switcher != null) {
			for(Tasks tasks : switcher.getAllTasks()) {
				tasks.onAfterProcess(context, data);
			}
		}
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		if (switcher != null) {
			for(Tasks tasks : switcher.getAllTasks()) {
				tasks.onBeforeProcess(context, data);
			}
		}
	}


	@Override
	public void onAfterItemProcess(ProcessContext context, Object data) throws Exception {
		if (switcher != null) {
			for(Tasks tasks : switcher.getAllTasks()) {
				tasks.onAfterItemProcess(context, data);
			}
		}
	}


	@Override
	public void onBeforeItemProcess(ProcessContext context, Object data) throws Exception {
		if (switcher != null) {
			for(Tasks tasks : switcher.getAllTasks()) {
				tasks.onBeforeItemProcess(context, data);
			}
		}
	}

	@Override
	public void setTasksProcessor(TasksProcessor processor) {
		if (switcher != null) {
			for(Tasks tasks : switcher.getAllTasks()) {
				tasks.setTasksProcessor(processor);
			}
		}
	}
}
