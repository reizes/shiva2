package com.reizes.shiva2.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import lombok.Data;

@Data
public class RestClientResponse implements Closeable {
	private InputStream response;
	private int responseCode;
	
	public void close() throws IOException {
		if (response != null) {
			response.close();
		}
	}
}
