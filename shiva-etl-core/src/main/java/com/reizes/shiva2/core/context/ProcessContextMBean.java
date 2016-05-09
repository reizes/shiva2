package com.reizes.shiva2.core.context;

public interface ProcessContextMBean {
	public void put(String name, Object value);
	public Object get(String name);
	public String getExecutionStatusValue();
	public void setExecutionStatusValue(String executionStatus);
	public String getProcessStatusValue();
	public void setProcessStatusValue(String processStatus);
	public long getItemCount();
	public Object getProcessParameter();
	public long getSkipCount() ;
}
