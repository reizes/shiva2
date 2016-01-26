package com.reizes.shiva2.etl.core.mock;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class MockWriter extends Writer {

	LinkedList<Object> outputList;
	
	public MockWriter() {
		outputList=new LinkedList<Object>();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		write(cbuf);
	}
	
	@Override
	public void write(int b) throws IOException {
		//System.out.println((char)b);
		outputList.add(b);
	}
	
	@Override
	public void write(char[] b) throws IOException {
		//System.out.println(b);
		outputList.add(b);
	}
	
	@Override
	public void write(String b) throws IOException {
		//System.out.println(b);
		outputList.add(b);
	}
	
	public boolean equals(List<Object> in) {
		if (this.outputList.size()!=in.size()) return false;
		return outputList.containsAll(in) && in.containsAll(this.outputList);
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	public LinkedList<Object> getOutputList() {
		return outputList;
	}

}
