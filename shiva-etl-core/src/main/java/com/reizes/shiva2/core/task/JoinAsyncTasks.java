package com.reizes.shiva2.core.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.AsyncTasks;
import com.reizes.shiva2.core.AsyncTasksCallback;
import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.TasksProcessor;
import com.reizes.shiva2.core.TasksProcessorAware;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class JoinAsyncTasks extends AbstractTask implements AsyncTasksCallback, TasksProcessorAware, ProcessContextAware, BeforeProcessAware, AfterProcessAware {
	final private List<AsyncTasks> asyncTasksArray;
	private long milli = 0;
	private AtomicInteger finishedCount;
	private List<Object> resultList;
	
	public JoinAsyncTasks(AsyncTasks... asyncTasks) {
		this.asyncTasksArray = Arrays.asList(asyncTasks);
		registerCallback();
	}

	public JoinAsyncTasks(long milli, AsyncTasks... asyncTasks) {
		this.asyncTasksArray = Arrays.asList(asyncTasks);
		this.milli = milli;
		registerCallback();
	}
	
	public JoinAsyncTasks(List<AsyncTasks> asyncTasks) {
		this.asyncTasksArray = asyncTasks;
		registerCallback();
	}

	public JoinAsyncTasks(long milli, List<AsyncTasks> asyncTasks) {
		this.asyncTasksArray = asyncTasks;
		this.milli = milli;
		registerCallback();
	}
	
	private void registerCallback() {
		for(AsyncTasks asyncTasks : asyncTasksArray) {
			asyncTasks.addOnFinishCallback(this);
		}
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		if (asyncTasksArray.size()>0) {
			resultList = new ArrayList<>();
			finishedCount = new AtomicInteger(0);
			for(AsyncTasks asyncTasks : asyncTasksArray) {
				asyncTasks.doProcess(input);
			}
			
			synchronized(this) {
				wait(this.milli);
			}
			
			return resultList;
		} else {
			return input;
		}
	}

	@Override
	public synchronized void callback(Object result) {
		resultList.add(result);
		if (finishedCount.incrementAndGet() == this.asyncTasksArray.size()) {
			this.notify();
		}
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		for(AsyncTasks asyncTasks : asyncTasksArray) {
			asyncTasks.onAfterProcess(context, data);
		}
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		for(AsyncTasks asyncTasks : asyncTasksArray) {
			asyncTasks.onBeforeProcess(context, data);
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		for(AsyncTasks asyncTasks : asyncTasksArray) {
			asyncTasks.setProcessContext(context);
		}
	}

	@Override
	public void setTasksProcessor(TasksProcessor processor) {
		for(AsyncTasks asyncTasks : asyncTasksArray) {
			asyncTasks.setTasksProcessor(processor);
		}
	}

}
