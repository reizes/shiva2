package com.reizes.shiva2.core.reader;

import java.io.IOException;
import java.io.InputStream;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;

public class InputStreamExtractor extends AbstractExtractor implements AfterProcessAware {

	protected InputStream inputStream;
	
	public InputStreamExtractor() {
	}
	
	public InputStreamExtractor(InputStream inputStream) {
		this.setInputStream(inputStream);
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		if (this.inputStream!=null) {
			return extractBinaryData(input);
		} 
		throw new InvalidPropertyException("inputStream is null!");
	}
	
	protected Object extractBinaryData(Object input) throws Exception {
		Object output=null;
		do {
			int data=inputStream.read();
			if (data<0) break;
			output=startProcessItem(data);
		}while(true);
		return output;
	}
	
	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws IOException {
		if (this.inputStream!=null) {
			this.inputStream.close();
			this.inputStream=null;
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
