package com.reizes.shiva2.core;

import java.util.Iterator;

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
			for (Iterator<Task> iter = this.elementList.iterator(); iter.hasNext();) {
				Task next = iter.next();
				if (next instanceof TasksProcessorAware)
					((TasksProcessorAware)next).setTasksProcessor(processor);
			}
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		super.setProcessContext(context);
		if (this.elementList != null) {
			for (Iterator<Task> iter = this.elementList.iterator(); iter.hasNext();) {
				Task next = iter.next();
				if (next instanceof ProcessContextAware)
					((ProcessContextAware)next).setProcessContext(context);
			}
		}
	}

	@Override
	protected void setListenerFrom(TasksBase element) {
		super.setListenerFrom(element);
		if (this.elementList != null) {
			for (Iterator<Task> iter = this.elementList.iterator(); iter.hasNext();) {
				Task next = iter.next();
				if (next instanceof TasksBase) {
					((TasksBase)next).setListenerFrom(element);
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
