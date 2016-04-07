package com.reizes.shiva2.core.task;

import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.Task;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

/**
 * 
 * @author reizes
 * @since 2.1.5 - skipNullLine, normalize property 추가
 */
public class OffsetLimitProcessor implements Task, BeforeProcessAware, ProcessContextAware {
	private long offset = 0;
	private long limit = -1;
	private long curItemCount = 0;
	private ProcessContext context;

	public OffsetLimitProcessor() {

	}

	public OffsetLimitProcessor(long offset) {
		this.offset = offset;
	}

	public OffsetLimitProcessor(long offset, long limit) {
		this.offset = offset;
		this.limit = limit;
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		this.curItemCount++;

		if (offset > 0 && this.curItemCount <= offset) {
			this.context.setExecutionStatus(ExecutionStatus.SKIP);
			return null;
		}

		if (limit >= 0 && this.curItemCount-offset > limit) {
			this.context.setExecutionStatus(ExecutionStatus.STOP);
			return null;
		}

		return input;
	}

	public long getOffset() {
		return offset;
	}

	public OffsetLimitProcessor setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public long getLimit() {
		return limit;
	}

	public OffsetLimitProcessor setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		this.curItemCount = 0;
	}
}
