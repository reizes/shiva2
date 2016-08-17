package com.reizes.shiva2.core.task;

import java.util.Map;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;
import com.reizes.shiva2.utils.JsonUtils;

public class JsonStringToMap extends AbstractTask implements ProcessContextAware {
	private boolean isSkipOnJsonSyntaxException = true;
	private boolean isBypassOnJsonSyntaxException = false;
	private ProcessContext context;

	@Override
	public Object doProcess(Object input) throws Exception {
		try {
			Map<String, Object> resultMap = JsonUtils.fromJson((String)input);
			return resultMap;
		} catch(Exception ex) {
			ex.printStackTrace();
			if (isSkipOnJsonSyntaxException && !isBypassOnJsonSyntaxException) {
				this.context.setExecutionStatus(ExecutionStatus.SKIP);
			}
			if (!isSkipOnJsonSyntaxException && isBypassOnJsonSyntaxException) {
				return input;
			}
			//throw ex;
		}
		return input;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}

}
