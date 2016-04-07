package com.reizes.shiva2.core.task;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class JsonStringToMap extends AbstractTask implements ProcessContextAware {
	private Gson gson = new Gson();
	private boolean isSkipOnJsonSyntaxException = true;
	private boolean isBypassOnJsonSyntaxException = false;
	private ProcessContext context;

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		try {
			Map<String, Object> resultMap = gson.fromJson((String)input, HashMap.class);
			return resultMap;
		} catch(JsonSyntaxException ex) {
			ex.printStackTrace();
			if (isSkipOnJsonSyntaxException && !isBypassOnJsonSyntaxException) {
				this.context.setExecutionStatus(ExecutionStatus.SKIP);
			}
			if (!isSkipOnJsonSyntaxException && isBypassOnJsonSyntaxException) {
				return input;
			}
			throw ex;
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}

}
