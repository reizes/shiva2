package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class Tasks extends TasksBase implements TasksProcessorAware, BeforeProcessAware,
	AfterProcessAware {

	@Override
	public Object doProcess(Object input) throws Exception {
		return processElementList(input, null);
	}

	@Override
	public void setTasksProcessor(TasksProcessor processor) {
		if (this.elementList != null) {
			for (Task task : this.elementList) {
				if (task instanceof TasksProcessorAware)
					((TasksProcessorAware)task).setTasksProcessor(processor);
			}
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		super.setProcessContext(context);
		if (this.elementList != null) {
			for (Task task : this.elementList) {
				if (task instanceof ProcessContextAware)
					((ProcessContextAware)task).setProcessContext(context);
			}
		}
	}

	@Override
	protected void setListenerFrom(TasksBase element) {
		super.setListenerFrom(element);
		if (this.elementList != null) {
			for (Task task : this.elementList) {
				if (task instanceof TasksBase) {
					((TasksBase)task).setListenerFrom(element);
				}
			}
		}
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		callBeforeProcessAware(context, data);
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		callAfterProcessAware(context, data);
	}

}
