package com.reizes.shiva2.http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.Data;

@Data
public class RestClientResponse implements Closeable {
	private InputStream response;
	private int responseCode;
	private String statusText;
	
	public void close() throws IOException {
		if (response != null) {
			response.close();
		}
	}
	
	public String getResponseText() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(response));
		StringBuilder sb = new StringBuilder();
		String line = null;
		do {
			line = br.readLine();
			if (line!=null) {
				sb.append(line).append("\n");
			}
		} while(line!=null);
		br.close();
		
		return sb.toString();
	}
}
