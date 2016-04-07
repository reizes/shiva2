package com.reizes.shiva2.core.reader;

import java.io.Reader;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class ReaderFixedLengthExtractor extends ReaderExtractor implements ProcessContextAware {

	private int itemLength=1;	// length of byte to read per 1 process
	private ProcessContext context;
	
	public ReaderFixedLengthExtractor() {
	}
	
	public ReaderFixedLengthExtractor(Reader reader) {
		this.setReader(reader);
	}
	
	@Override
	protected Object extractCharacterData(Object input) throws Exception {
		if (this.itemLength>=1) {
			Object output=null;
			do {
				char[] buffer=new char[this.itemLength];
				int data=getReader().read(buffer);
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
