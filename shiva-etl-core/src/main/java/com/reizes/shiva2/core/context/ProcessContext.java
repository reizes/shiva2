package com.reizes.shiva2.core.context;

import java.util.HashMap;
import java.util.Map;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.ProcessStatus;
import com.reizes.shiva2.core.TasksProcessor;

public class ProcessContext implements Cloneable {
	private TasksProcessor processor;
	private Object processParameter; // 2.1.0 ETL Process의 doProcess로 들어온 파라메터 저장
	private Map<String, Object> map;
	private ExecutionStatus executionStatus;
	private ProcessStatus processStatus;
	private boolean throwException = true;
	private long itemCount = 0;
	private long skipCount = 0;	// 2.1.1-SNAPSHOT 

	public ProcessContext() {
		
	}
	
	public ProcessContext(TasksProcessor processor) {
		this.processor = processor;
		this.map = new HashMap<String, Object>();
		initContext();
	}

	public void initContext() {
		this.executionStatus = ExecutionStatus.CONTINUE;
		this.processStatus = ProcessStatus.UNKNOWN;
	}

	public TasksProcessor getProcessor() {
		return processor;
	}

	public synchronized void put(String name, Object value) {
		this.map.put(name, value);
	}

	public synchronized Object get(String name) {
		return this.map.get(name);
	}

	public synchronized ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public synchronized void setExecutionStatus(ExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}

	public synchronized ProcessStatus getProcessStatus() {
		return processStatus;
	}

	public synchronized void setProcessStatus(ProcessStatus processStatus) {
		this.processStatus = processStatus;
	}

	public synchronized boolean isThrowException() {
		return throwException;
	}

	public synchronized void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	public synchronized long getItemCount() {
		return itemCount;
	}

	public synchronized void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}

	public synchronized Map<String, Object> getContextDataMap() {
		return map;
	}

	public synchronized void setContextDataMap(Map<String, Object> map) {
		this.map = map;
	}

	public synchronized Object getProcessParameter() {
		return processParameter;
	}

	public synchronized void setProcessParameter(Object processParameter) {
		this.processParameter = processParameter;
	}

	public synchronized long getSkipCount() {
		return skipCount;
	}

	public synchronized void setSkipCount(long skipCount) {
		this.skipCount = skipCount;
	}
	
	public synchronized ProcessContext clone() {
		ProcessContext clonedContext = new ProcessContext();
		clonedContext.processor = processor;
		clonedContext.processParameter = processParameter;
		clonedContext.map = new HashMap<String, Object>();
		clonedContext.map.putAll(map);
		clonedContext.executionStatus = executionStatus;
		clonedContext.processStatus = processStatus;
		clonedContext.throwException = throwException;
		clonedContext.itemCount = itemCount;
		clonedContext.skipCount = skipCount;
		
		return clonedContext;
	}
}
