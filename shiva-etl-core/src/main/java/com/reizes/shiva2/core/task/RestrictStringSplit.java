package com.reizes.shiva2.core.task;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;
import com.reizes.shiva2.core.task.StringSplit;

/**
 * 스트링을 구분자로 구분한 배열로 변환하는 트랜스포머.
 * (구분된 배열 크기가 지정된 크기와 같지 않으면 SKIP)
 *
 * @author inho
 * @since 2010-03-23
 */
public class RestrictStringSplit extends StringSplit implements ProcessContextAware {

	/** The context. */
	private ProcessContext context;

	/** The expected split length. */
	private Integer expectedSplitLength;

	/**
	 * Do process.
	 *
	 * @param input String
	 * @return String[]
	 * @throws Exception -
	 * @see com.reizes.shiva2.core.task.StringSplit#doProcess(java.lang.Object)
	 */
	public Object doProcess(Object input) throws Exception {
		String[] result = (String[])super.doProcess(input);

		if (result == null || result.length < 1) {
			//input.log.warn("Input Data is null");
			context.setExecutionStatus(ExecutionStatus.SKIP);
			return null;
		}

		if (expectedSplitLength != null && result.length != expectedSplitLength) {
			//log.warn("Expected Split Length : " + expectedSplitLength + " But Actual : " + result.length + "\n"
			//	+ input);
			context.setExecutionStatus(ExecutionStatus.SKIP);
			return null;
		}

		return result;
	}

	/**
	 * Gets the expected split length.
	 *
	 * @return the expected split length
	 */
	public Integer getExpectedSplitLength() {
		return expectedSplitLength;
	}

	/**
	 * Sets the expected split length.
	 *
	 * @param expectedSplitLength the new expected split length
	 */
	public void setExpectedSplitLength(Integer expectedSplitLength) {
		this.expectedSplitLength = expectedSplitLength;
	}

	/**
	 * Sets the process context.
	 *
	 * @param context ProcessContext
	 * @see com.reizes.shiva2.core.context.ProcessContextAware#setProcessContext(com.reizes.shiva2.core.context.ProcessContext)
	 */
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}
}
