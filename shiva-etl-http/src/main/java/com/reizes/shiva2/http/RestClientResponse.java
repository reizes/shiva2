package com.reizes.shiva2.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.BufferedHttpEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.reizes.shiva2.utils.JsonUtils;

import lombok.Getter;
import lombok.Setter;

public class RestClientResponse {
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
	
	public Map<String, Object> getResponseJson() throws JsonParseException, JsonMappingException, IOException {
		return responseText!=null?JsonUtils.fromJson((String)responseText):null;
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
	
	public static RestClientResponse fromHttpResponse(HttpResponse httpResponse)  throws IOException {
		RestClientResponse restClientResponse = new RestClientResponse();
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			entity = new BufferedHttpEntity(entity);
			restClientResponse.setResponse(entity.getContent());
		}

		StatusLine statusLine = httpResponse.getStatusLine();
		restClientResponse.setResponseCode(statusLine.getStatusCode());
		restClientResponse.setStatusText(statusLine.toString());
		Header[] responseHeaders = httpResponse.getAllHeaders();
		for (int i = 0; i < responseHeaders.length; i++) {
			Header header = responseHeaders[i];
			restClientResponse.putHeader(header.getName(), header.getValue());
		}
		
		return restClientResponse;
	}
}
