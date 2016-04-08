package com.reizes.shiva2.core.task;

import java.util.ArrayList;
import java.util.List;

import com.reizes.shiva2.core.AsyncTasks;

public class JoinAsyncTasks extends AbstractTask {
	private AsyncTasks[] asyncTasksArray;
	private long milli = 0;
	
	public JoinAsyncTasks(AsyncTasks... asyncTasks) {
		this.asyncTasksArray = asyncTasks;
	}

	public JoinAsyncTasks(long milli, AsyncTasks... asyncTasks) {
		this.asyncTasksArray = asyncTasks;
		this.milli = milli;
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		List<Object> resultList = new ArrayList<>();
		for(AsyncTasks asyncTasks : asyncTasksArray) {
			asyncTasks.join(this.milli);
			resultList.add(asyncTasks.getResult());
		}
		
		return resultList;
	}

}
