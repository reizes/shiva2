package com.reizes.shiva2.core.reader;

import java.io.IOException;
import java.io.Reader;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;

public class ReaderExtractor extends AbstractExtractor implements
		AfterProcessAware {
	
	private Reader reader;
	
	public ReaderExtractor() {
	}
	
	public ReaderExtractor(Reader reader) {
		this.setReader(reader);
	}
	
	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws IOException {
		if (this.reader!=null) {
			this.reader.close();
			this.reader=null;
		}
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		if (this.reader!=null) {
			return extractCharacterData(input);
		} 
		throw new InvalidPropertyException("reader is null!");
	}
	
	protected Object extractCharacterData(Object input) throws Exception {
		Object output=null;
		do {
			int data=reader.read();
			if (data<0) break;
			output=startProcessItem(data);
		}while(true);
		return output;
	}

	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}

}
