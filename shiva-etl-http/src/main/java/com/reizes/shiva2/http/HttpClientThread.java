package com.reizes.shiva2.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientThread extends Thread {
	private CloseableHttpClient client;
	private HttpUriRequest request;
	private HttpHost host;
	private HttpRequestCallback callback;

	public HttpClientThread(CloseableHttpClient client, HttpHost host, HttpUriRequest request, HttpRequestCallback callback) {
		this.client = client;
		this.request = request;
		this.callback = callback;
	}

	public HttpClientThread(CloseableHttpClient client, HttpHost host, HttpUriRequest request) {
		this.client = client;
		this.request = request;
		this.callback = null;
	}
	
	@Override
	public void run() {
		try {
			HttpResponse httpResponse = client.execute(host, request);
			if (this.callback!=null) {
				callback.onHttpResponse(httpResponse);
			}
			EntityUtils.consume(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
