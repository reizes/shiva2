package com.reizes.shiva2.core.reader;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;

public class InputStreamLeadByteExtractor extends InputStreamExtractor implements ProcessContextAware {
	private ProcessContext context;
	private Byte leadByte;
	private Byte escapeByte;
	
	public InputStreamLeadByteExtractor() {
	}
	
	public InputStreamLeadByteExtractor(InputStream inputStream) {
		this.setInputStream(inputStream);
	}
	
	@Override
	protected Object extractBinaryData(Object input) throws Exception {
		if (leadByte==null) return super.extractBinaryData(input);
		Object output=null;
		LinkedList<Byte> list=new LinkedList<Byte>(); 
		boolean validData=false;
		boolean escape=false;
		do {
			int data=inputStream.read();
			if (data<0) break;
			if (!escape && data==leadByte) {
				if (validData) {
					validData=false;
					byte[] processData=new byte[list.size()];
					int index=0;
					for(Iterator<Byte> iter=list.iterator();iter.hasNext();) {
						processData[index++]=iter.next();
					}
					this.context.put("extractedDataLength", list.size());
					output=startProcessItem(processData);
				} else {
					validData=true;
					list.clear();
				}
				continue;
			}
			if (!validData) continue; 
			if (escape==false && escapeByte!=null && data==escapeByte) {
				escape=true;
				continue;
			}
			escape=false;
			list.add(new Byte((byte) data));
		}while(true);
		return output;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context=context;
	}

	public Byte getLeadByte() {
		return leadByte;
	}

	public void setLeadByte(Byte leadByte) {
		this.leadByte = leadByte;
	}

	public Byte getEscapeByte() {
		return escapeByte;
	}

	public void setEscapeByte(Byte escapeByte) {
		this.escapeByte = escapeByte;
	}

}
