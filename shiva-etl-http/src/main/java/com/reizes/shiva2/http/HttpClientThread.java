package com.reizes.shiva2.http;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.reizes.shiva2.core.ExceptionListener;

public class HttpClientThread extends Thread {
	private CloseableHttpClient client;
	private HttpUriRequest request;
	private HttpHost host;
	private HttpRequestCallback callback;
	private ExceptionListener exceptionListener;

	public HttpClientThread(CloseableHttpClient client, HttpHost host, HttpUriRequest request, HttpRequestCallback callback) {
		this.client = client;
		this.host = host;
		this.request = request;
		this.callback = callback;
	}

	public HttpClientThread(CloseableHttpClient client, HttpHost host, HttpUriRequest request) {
		this.client = client;
		this.host = host;
		this.request = request;
		this.callback = null;
	}
	
	@Override
	public void run() {
		try {
			CloseableHttpResponse httpResponse = client.execute(host, request);
			try {
				if (this.callback!=null) {
					callback.onHttpResponse(RestClientResponse.fromHttpResponse(httpResponse));
				}
			} finally {
				EntityUtils.consume(httpResponse.getEntity());
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (exceptionListener!=null) {
				exceptionListener.onException(null, request, e);
			}
		}
	}

	public ExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public void setExceptionListener(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

}
