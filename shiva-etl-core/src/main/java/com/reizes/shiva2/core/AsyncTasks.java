package com.reizes.shiva2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class AsyncTasks implements Task, TasksHolder, AsyncTasksCallback, TasksProcessorAware, ProcessContextAware, BeforeProcessAware, AfterProcessAware {
	private ProcessContext context;
	private ProcessorThread thread = new ProcessorThread();
	private Object result;
	
	private class ProcessorThread extends Thread {
		private List<AsyncTasksCallback> onFinish = new ArrayList<>();
		private AsyncTasksCallback callbackToParent;
		private List<AsyncTasksCallback> onException = new ArrayList<>();
		private Tasks tasks = new Tasks();
		private SynchronousQueue<Object> queue = new SynchronousQueue<Object>();
		private AtomicBoolean isContinue = new AtomicBoolean(true);
		
		private void callCallback(List<AsyncTasksCallback> list, Object data) {
			for(AsyncTasksCallback callback : list) {
				callback.callback(data);
			}
		}
		
		@Override
		public void run() {
			do {
				try {
					Object object = queue.poll(300, TimeUnit.MILLISECONDS);
					if (object==null) {
						continue;
					}
					Object result = tasks.processElementList(object, null);
					callbackToParent.callback(result);
					callCallback(onFinish, result);
				} catch (Exception e) {
					e.printStackTrace();
					callCallback(onException, result);
				}
				if (tasks.context.getExecutionStatus() == ExecutionStatus.STOP) break;
			} while(isContinue.get());
			try {
				queue.clear();
				tasks.onAfterProcess(context, null);
			} catch (Exception e) {
				e.printStackTrace();
				callCallback(onException, null);
			}
		}
		
	}
	
	public AsyncTasks() {
		this.thread.callbackToParent = this;
	}
	
	public AsyncTasks(AsyncTasksCallback onFinish) {
		this.thread.callbackToParent = this;
		this.thread.onFinish.add(onFinish);
	}
	
	public AsyncTasks(AsyncTasksCallback onFinish, AsyncTasksCallback onException) {
		this.thread.callbackToParent = this;
		this.thread.onFinish.add(onFinish);
		this.thread.onException.add(onException);
	}
	
	public AsyncTasks addOnFinishCallback(AsyncTasksCallback onFinish) {
		this.thread.onFinish.add(onFinish);
		return this;
	}
	
	public AsyncTasks addOnExceptionCallback(AsyncTasksCallback onException) {
		this.thread.onException.add(onException);
		return this;
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		if (!thread.isAlive()) {
			thread.start();
		}
		thread.queue.put(input);
		return input;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
		this.thread.tasks.setProcessContext(this.thread.tasks.isShareProcessContext() ? context : new ProcessContext());
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

	@Override
	public List<Task> getTasks() {
		return this.thread.tasks.getTasks();
	}

	@Override
	public TasksHolder setTasks(List<Task> elementList) {
		this.thread.tasks.setTasks(elementList);
		return this;
	}

	@Override
	public TasksHolder setTask(Task element) {
		this.thread.tasks.setTask(element);
		return this;
	}

	@Override
	public TasksHolder addTask(Task element) {
		this.thread.tasks.addTask(element);
		return this;
	}

	@Override
	public TasksHolder addTasks(List<Task> elementList) {
		this.thread.tasks.addTasks(elementList);
		return this;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		this.thread.isContinue.set(false);
		this.thread.queue.put(new Object());	// wake
		this.thread.join();
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		this.thread.tasks.onBeforeProcess(context, data);
	}

	@Override
	public void setTasksProcessor(TasksProcessor processor) {
		this.thread.tasks.setTasksProcessor(processor);
	}

	public boolean isShareProcessContext() {
		return this.thread.tasks.isShareProcessContext();
	}

	public void setShareProcessContext(boolean shareProcessContext) {
		this.thread.tasks.setShareProcessContext(shareProcessContext);
	}
}
