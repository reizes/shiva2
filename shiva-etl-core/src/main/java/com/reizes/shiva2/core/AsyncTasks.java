package com.reizes.shiva2.core;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class AsyncTasks extends Tasks implements AsyncTasksCallback {
	private ProcessContext context;
	private ProcessorThread thread = new ProcessorThread();
	private Object result;
	
	private class ProcessorThread extends Thread {
		private AsyncTasksCallback onFinish;
		private AsyncTasksCallback callbackToParent;
		private AsyncTasksCallback onException;
		private Object input;
		
		public synchronized void start(Object input) {
			this.input = input;
			super.start();
		}
		
		@Override
		public void run() {
			try {
				Object result = processElementList(input, null);
				callbackToParent.callback(result);
				if (onFinish!=null) {
					onFinish.callback(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (onException!=null) {
					onException.callback(e);
				}
			}
		}
		
	}
	
	public AsyncTasks() {
		this.thread.callbackToParent = this;
	}
	
	public AsyncTasks(AsyncTasksCallback onFinish) {
		this.thread.callbackToParent = this;
		this.thread.onFinish = onFinish;
	}
	
	public AsyncTasks(AsyncTasksCallback onFinish, AsyncTasksCallback onException) {
		this.thread.callbackToParent = this;
		this.thread.onFinish = onFinish;
		this.thread.onException = onException;
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		thread.start(input);
		return input;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = isShareProcessContext() ? context.clone() : new ProcessContext();
		
		if (this.elementList != null) {
			for (Task task : this.elementList) {
				if (task instanceof ProcessContextAware)
					((ProcessContextAware)task).setProcessContext(this.context);
			}
		}
	}

	public void join() throws InterruptedException {
		this.thread.join();
	}

	public void join(long millis) throws InterruptedException {
		this.thread.join(millis);
	}
	
	public Object getResult() {
		return this.result;
	}

	@Override
	public void callback(Object result) {
		this.result = result;
	}
}
