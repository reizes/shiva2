package com.reizes.shiva2.core.extractor;

import java.io.Reader;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class ReaderDelimiterExtractor extends ReaderExtractor implements ProcessContextAware {

	private char delimiter='\n';
	private ProcessContext context;
	
	public ReaderDelimiterExtractor() {
	}
	
	public ReaderDelimiterExtractor(Reader reader) {
		this.setReader(reader);
	}
	
	@Override
	protected Object extractCharacterData(Object input) throws Exception {
		Object output=null;
		StringBuilder sb=new StringBuilder();
		do {
			int data=getReader().read();
			if (data<0) break;
			if (data==delimiter) {
				this.context.put("extractedDataLength", sb.length());
				output=startProcessItem(sb.toString().toCharArray());
				sb=new StringBuilder();
			} else {
				sb.append((char)data);
			}
		}while(true);
		if (sb.length()>0) {
			this.context.put("extractedDataLength", sb.length());
			output=startProcessItem(sb.toString().toCharArray());
			sb=new StringBuilder();
		}
		return output;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context=context;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

}
