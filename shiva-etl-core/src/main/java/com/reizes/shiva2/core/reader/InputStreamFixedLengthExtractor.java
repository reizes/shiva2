package com.reizes.shiva2.core.reader;

import java.io.InputStream;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class InputStreamFixedLengthExtractor extends InputStreamExtractor implements ProcessContextAware {

	private int itemLength=1;	// length of byte to read per 1 process
	private ProcessContext context;
	
	public InputStreamFixedLengthExtractor() {
	}
	
	public InputStreamFixedLengthExtractor(InputStream inputStream) {
		this.setInputStream(inputStream);
	}
	
	@Override
	protected Object extractBinaryData(Object input) throws Exception {
		if (this.itemLength>=1) {
			Object output=null;
			do {
				byte[] buffer=new byte[this.itemLength];
				int data=inputStream.read(buffer);
				if (data<0) break;
				this.context.put("extractedDataLength", data);
				output=startProcessItem(buffer);
			}while(true);
			return output;
		}
		throw new InvalidPropertyException("itemLength must be >=1");
	}

	public int getItemLength() {
		return itemLength;
	}

	public void setItemLength(int itemLength) {
		this.itemLength = itemLength;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context=context;
	}

}
