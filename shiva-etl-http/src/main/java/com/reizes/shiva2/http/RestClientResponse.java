package com.reizes.shiva2.http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

public class RestClientResponse implements Closeable {
	private InputStream response;
	@Getter
	@Setter
	private int responseCode;
	@Getter
	@Setter
	private String statusText;
	@Getter
	private Map<String, String> headers = new HashMap<String, String>();
	@Getter
	private String responseText;
	private Gson gson = new Gson();
	
	public void close() throws IOException {
		if (response != null) {
			response.close();
		}
	}
	
	public void putHeader(String key, String value) {
		this.headers.put(key, value);
	}
	
	public void setResponse(InputStream is) {
		try {
			this.responseText = getResponseText(is);
		} catch (IOException e) {
			this.responseText = e.getMessage();
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getResponseJson() {
		return responseText!=null?gson.fromJson((String)responseText, HashMap.class):null;
	}
	
	private String getResponseText(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		do {
			line = br.readLine();
			if (line!=null) {
				sb.append(line).append("\n");
			}
		} while(line!=null);
		br.close();
		is.close();
		return sb.toString();
	}
}
