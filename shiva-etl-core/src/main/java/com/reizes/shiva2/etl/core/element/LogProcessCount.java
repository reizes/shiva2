package com.reizes.shiva2.etl.core.element;

import java.io.OutputStream;

import com.reizes.shiva2.etl.core.EtlElement;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.context.ProcessContextAware;

/**
 * 지정 카운트마다 처리 중인 숫자를 기록
 * @author reizes
 * @since 2.0.2
 * @since 2010.5.4
 */
public class LogProcessCount implements EtlElement, ProcessContextAware {
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
	 * @param input
	 * @return
	 * @throws Exception
	 * @see com.reizes.shiva2.etl.core.EtlElement#doProcess(java.lang.Object)
	 */
	@Override
	public Object doProcess(Object input) throws Exception {
		long count = initialCount + this.context.getItemCount();

		if ((count + 1) % step == 0l) {
			String outputMsg = String.format(msgFormat, count + 1);
			outputStream.write(outputMsg.getBytes());
			outputStream.flush();
		}

		return input;
	}

	/**
	 * @param context
	 * @see com.reizes.shiva2.etl.core.context.ProcessContextAware#setProcessContext(com.reizes.shiva2.etl.core.context.ProcessContext)
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
