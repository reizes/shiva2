package com.reizes.shiva2.etl.core.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class MockOutputStream extends OutputStream {

	LinkedList<Object> outputList;
	
	public MockOutputStream() {
		outputList=new LinkedList<Object>();
	}
	
	@Override
	public void write(int b) throws IOException {
		//System.out.println(b);
		outputList.add(b);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		//for(byte bb:b) System.out.print(bb+",");
		//System.out.println("");
		outputList.add(b.clone());
	}
	
	public boolean equals(List<Object> in) {
		if (this.outputList.size()!=in.size()) return false;
		return outputList.containsAll(in) && in.containsAll(this.outputList);
	}
	
	public List<Object> getOutputList() {
		return this.outputList;
	}

}
