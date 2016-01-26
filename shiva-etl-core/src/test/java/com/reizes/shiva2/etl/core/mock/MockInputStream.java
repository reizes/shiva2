package com.reizes.shiva2.etl.core.mock;

import java.io.IOException;
import java.io.InputStream;

public class MockInputStream extends InputStream {
	
	private byte[] inputData;
	private int currentPointer=0;

	@Override
	public int read() throws IOException {
		if (currentPointer==inputData.length) return -1;
		return inputData[currentPointer++];
	}

	public byte[] getInputData() {
		return inputData;
	}

	public void setInputData(byte[] inputData) {
		this.inputData = inputData;
	}

}
