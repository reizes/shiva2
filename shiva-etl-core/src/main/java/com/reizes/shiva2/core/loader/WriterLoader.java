package com.reizes.shiva2.core.loader;

import java.io.IOException;
import java.io.Writer;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.InvalidClassException;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;

public class WriterLoader extends AbstractLoader implements AfterProcessAware {

	private Writer writer;

	public WriterLoader() {
	}

	public WriterLoader(Writer writer) {
		this.setWriter(writer);
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		if (writer == null) {
			throw new InvalidPropertyException("writer is NULL");
		}
		if (input instanceof char[]) {
			writer.write((char[])input);
		} else if (input instanceof Integer) {
			writer.write((Integer)input);
		} else if (input instanceof String) {
			writer.write((String)input);
		} else {
			throw new InvalidClassException("WriterLoader can not support " + input.getClass().getName());
		}

		return input;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws IOException {
		if (this.writer != null) {
			this.writer.flush();
			this.writer.close();
			this.writer = null;
		}
	}

}
