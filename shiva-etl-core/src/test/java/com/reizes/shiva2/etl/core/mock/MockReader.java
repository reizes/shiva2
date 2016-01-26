package com.reizes.shiva2.etl.core.mock;

import java.io.IOException;
import java.io.Reader;

public class MockReader extends Reader {
	
	private char[] inputData;
	private int currentPointer=0;

	@Override
	public void close() throws IOException {
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (currentPointer==inputData.length) return -1;
		int rest=inputData.length-currentPointer;
		int trans=Math.min(len,Math.min(cbuf.length-off, rest));
		System.arraycopy(inputData, currentPointer, cbuf, off, trans);
		currentPointer+=trans;
		return trans;
	}

	public char[] getInputData() {
		return inputData;
	}

	public void setInputData(char[] inputData) {
		this.inputData = inputData;
	}

}
