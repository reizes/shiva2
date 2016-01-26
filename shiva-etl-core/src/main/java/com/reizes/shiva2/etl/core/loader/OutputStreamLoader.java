package com.reizes.shiva2.etl.core.loader;

import java.io.IOException;
import java.io.OutputStream;

import com.reizes.shiva2.etl.core.AfterProcessAware;
import com.reizes.shiva2.etl.core.InvalidClassException;
import com.reizes.shiva2.etl.core.InvalidPropertyException;
import com.reizes.shiva2.etl.core.context.ProcessContext;

public class OutputStreamLoader extends AbstractLoader implements AfterProcessAware {

	private OutputStream outputStream;
	
	public OutputStreamLoader() {
	}
	
	public OutputStreamLoader(OutputStream outputStream) {
		this.setOutputStream(outputStream);
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		if (outputStream==null) throw new InvalidPropertyException("outputStream is NULL");
		if (input instanceof byte[]) outputStream.write((byte[])input);
		else if (input instanceof Integer) outputStream.write((Integer)input);
		else throw new InvalidClassException("OutputStreamLoader can not support "+input.getClass().getName());
		return input;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws IOException {
		if (this.outputStream!=null) {
			this.outputStream.flush();
			this.outputStream.close();
			this.outputStream=null;
		}
	}

}
