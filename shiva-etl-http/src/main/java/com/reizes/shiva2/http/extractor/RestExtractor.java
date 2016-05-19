package com.reizes.shiva2.http.extractor;

import java.net.URISyntaxException;
import java.util.Map;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.extractor.AbstractExtractor;
import com.reizes.shiva2.http.RestClient;
import com.reizes.shiva2.http.RestClientResponse;

public class RestExtractor extends AbstractExtractor implements AfterProcessAware {
	private RestClient client;
	private Map<String, String> headers;
	
	public RestExtractor(String host) throws URISyntaxException {
		this.client = new RestClient(host);
	}
	
	@Override
	public Object doProcess(Object requestUri) throws Exception {
		RestClientResponse resut = this.client.get((String)requestUri, headers);
		
		startProcessItem(resut);
		
		resut.close();
		return requestUri;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		this.client.close();
	}

}
