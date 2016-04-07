package com.reizes.shiva2.http.extractor;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import com.reizes.shiva2.core.reader.AbstractExtractor;
import com.reizes.shiva2.http.RestClient;

public class RestExtractor extends AbstractExtractor {
	private RestClient client;
	private Map<String, String> headers;
	
	public RestExtractor(String host) throws URISyntaxException {
		this.client = new RestClient(host);
	}
	
	@Override
	public Object doProcess(Object requestUri) throws Exception {
		InputStream is = this.client.get((String)requestUri, headers);
		
		startProcessItem(is);
		
		is.close();
		return requestUri;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}
