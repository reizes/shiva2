package com.reizes.shiva2.core.task;

import java.io.OutputStream;

import com.reizes.shiva2.core.Task;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

/**
 * Log processed item count 
 * @author reizes
 * @since 0.2.0
 * @since 2010.5.4
 */
public class LogProcessCount implements Task, ProcessContextAware {
	private ProcessContext context;
	private long step = 1000;
	private long initialCount = 0;
	private String msgFormat = "Current Process : %d";
	private OutputStream outputStream;
	
	public LogProcessCount() {
	}
	
	public LogProcessCount(long step) {
		this.step = step;
	}
	
	public LogProcessCount(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public LogProcessCount(OutputStream outputStream, long step) {
		this.outputStream = outputStream;
		this.step = step;
	}

	/**
	 * @param input Input Object
	 * @return Input Object (bypass)
	 * @throws Exception Exception object
	 * @see com.reizes.shiva2.core.Task#doProcess(java.lang.Object)
	 */
	@Override
	public Object doProcess(Object input) throws Exception {
		long count = initialCount + this.context.getItemCount();

		if ((count + 1) % step == 0l) {
			String outputMsg = String.format(msgFormat, count + 1);
			printMessage(outputMsg);
		}

		return input;
	}
	
	public void printMessage(String message) throws Exception {
		outputStream.write(message.getBytes());
		outputStream.flush();
	}

	/**
	 * @param context ProcessContext for this processor
	 * @see com.reizes.shiva2.core.context.ProcessContextAware#setProcessContext(com.reizes.shiva2.core.context.ProcessContext)
	 */
	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}

	public long getStep() {
		return step;
	}

	public LogProcessCount setStep(long step) {
		this.step = step;
		return this;
	}

	public String getMsgFormat() {
		return msgFormat;
	}

	public LogProcessCount setMsgFormat(String msgFormat) {
		this.msgFormat = msgFormat;
		return this;
	}

	public long getInitialCount() {
		return initialCount;
	}

	public LogProcessCount setInitialCount(long initialCount) {
		this.initialCount = initialCount;
		return this;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public LogProcessCount setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
		return this;
	}

}
