package com.reizes.shiva2.core;

import java.util.LinkedList;
import java.util.List;

import com.reizes.shiva2.core.context.NullContextException;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public abstract class TasksBase implements Task, TasksHolder, ProcessContextAware {
	private BeforeItemProcessListener beforeItemProcessListener;
	private AfterItemProcessListener afterItemProcessListener;
	private ExceptionListener exceptionListener;
	protected ProcessContext context;
	protected List<Task> elementList;
	private boolean shareProcessContext = true;

	public BeforeItemProcessListener getBeforeItemProcessListener() {
		return beforeItemProcessListener;
	}

	public void setBeforeItemProcessListener(BeforeItemProcessListener beforeItemProcessListener) {
		this.beforeItemProcessListener = beforeItemProcessListener;
	}

	public AfterItemProcessListener getAfterItemProcessListener() {
		return afterItemProcessListener;
	}

	public void setAfterItemProcessListener(AfterItemProcessListener afterItemProcessListener) {
		this.afterItemProcessListener = afterItemProcessListener;
	}

	protected void setListenerFrom(TasksBase element) {
		this.setAfterItemProcessListener(element.getAfterItemProcessListener());
		this.setBeforeItemProcessListener(element.getBeforeItemProcessListener());
		this.setExceptionListener(element.getExceptionListener());
	}

	protected void callBeforeItemProcessListener(ProcessContext context, Object item) {
		if (this.beforeItemProcessListener != null) {
			this.beforeItemProcessListener.onBeforeItemProcess(context, item);
		}
	}

	protected void callAfterItemProcessListener(ProcessContext context, Object item) {
		if (this.afterItemProcessListener != null) {
			this.afterItemProcessListener.onAfterItemProcess(context, item);
		}
	}

	protected void callExceptionListener(ProcessContext context, Object item, Exception e) {
		if (this.exceptionListener != null) {
			this.exceptionListener.onException(context, item, e);
		}
	}

	public ExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public void setExceptionListener(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

	protected Object processElementList(Object input, Task startElement) throws Exception {
		Object output = input;
		boolean started = false;

		if (this.context == null) {
			throw new NullContextException();
		}
		for (Task element : this.elementList) {
			if (started == false && startElement != null && startElement != element) {
				continue;
			}

			started = true;

			if (!(element instanceof TasksBase)) {
				callBeforeItemProcessListener(this.context, output);
			}

			Exception rethrow = null;

			if (this.context.getExecutionStatus() == ExecutionStatus.CONTINUE) {
				if (element instanceof BeforeItemProcessAware) {
					((BeforeItemProcessAware)element).onBeforeItemProcess(this.context, output);
				}
				if (this.context.getExecutionStatus() == ExecutionStatus.CONTINUE) {
					try {
						output = element.doProcess(output);
					} catch (InterruptException e) {
						rethrow = e;
						this.context.setExecutionStatus(ExecutionStatus.STOP);
					} catch (Exception e) {
						rethrow = e;
						this.context.setExecutionStatus(ExecutionStatus.STOP);
						callExceptionListener(this.context, output, e);
					}
				}
				if (element instanceof AfterItemProcessAware) {
					((AfterItemProcessAware)element).onAfterItemProcess(this.context, output);
				}
			}
			if (!(element instanceof TasksBase)) {
				callAfterItemProcessListener(this.context, output);
			}
			// check ExecutionStatus
			if (rethrow != null && this.context.isThrowException()) {
				// exception
				if (this.context.getExecutionStatus() == ExecutionStatus.STOP) {
					throw rethrow;
				}

				rethrow = null;
			}

			if (this.context.getExecutionStatus() == ExecutionStatus.SKIP) {
				break;
			}
			if (this.context.getExecutionStatus() == ExecutionStatus.STOP) {
				throw new InterruptException();
			}
		}
		if (this.context.getExecutionStatus() != ExecutionStatus.SKIP) {
			this.context.setItemCount(this.context.getItemCount() + 1);
		} else {
			this.context.setSkipCount(this.context.getSkipCount() + 1); // 2.1.1 - skip 카운트 도입 2012.7.3
		}

		return output;
	}

	protected void callBeforeProcessAware(ProcessContext context, Object data) throws Exception {
		if (this.elementList != null) {
			for (Task task : this.elementList) {
				if (task instanceof BeforeProcessAware) {
					((BeforeProcessAware)task).onBeforeProcess(context, data);
				}
			}
		}
	}

	protected void callAfterProcessAware(ProcessContext context, Object data) throws Exception {
		if (this.elementList != null) {
			for (Task task : this.elementList) {
				if (task instanceof AfterProcessAware) {
					((AfterProcessAware)task).onAfterProcess(context, data);
				}
			}
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		if (isShareProcessContext()) {
			this.context = context;
		}
	}

	public ProcessContext getProcessContext() {
		return this.context;
	}

	@Override
	public List<Task> getTasks() {
		return elementList;
	}

	@Override
	public TasksHolder setTasks(List<Task> elementList) {
		this.elementList = new LinkedList<Task>();
		this.addTasks(elementList);
		
		return this;
	}

	@Override
	public TasksHolder addTasks(List<Task> elementList) {
		for (Task element : elementList) {
			this.addTask(element);
		}
		
		return this;
	}

	@Override
	public TasksHolder setTask(Task element) {
		this.elementList = new LinkedList<>();
		this.elementList.add(element);
		return this;
	}

	@Override
	public TasksHolder addTask(Task element) {
		if (this.elementList == null) {
			this.setTask(element);
		} else {
			this.elementList.add(element);
		}
		return this;
	}
	
	public TasksHolder clearTasks() {
		this.elementList = new LinkedList<>();
		return this;
	}

	public synchronized boolean isShareProcessContext() {
		return shareProcessContext;
	}

	public synchronized void setShareProcessContext(boolean shareProcessContext) {
		this.shareProcessContext = shareProcessContext;
	}

}
