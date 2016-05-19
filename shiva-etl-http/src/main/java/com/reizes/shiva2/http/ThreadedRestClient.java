package com.reizes.shiva2.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import lombok.Getter;
import lombok.Setter;

public class ThreadedRestClient extends RestClient {
	@Getter
	@Setter
	private int maxTotal = 20;
	@Getter
	@Setter
	private int maxPerRoute = 5;
	
	private ExecutorService executor = Executors.newCachedThreadPool();

	public ThreadedRestClient(URI uri) {
		super(uri);
	}

	public ThreadedRestClient(String uri) throws URISyntaxException {
		super(uri);
	}

	@Override
	protected CloseableHttpClient initHttpClient() {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(maxTotal);
		connManager.setDefaultMaxPerRoute(maxPerRoute);
		connManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build());
		CloseableHttpClient client = HttpClients.custom().
		    setConnectionManager(connManager).build();
		
		return client;
	}

	public void requestAsync(Method method, String requestUri, Map<String, String> headers,
			HttpEntity requestEntity, HttpRequestCallback callback) throws IOException {
		URI uri = this.getUri();
		HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		HttpUriRequest request = buildRequest(method, requestUri, headers, requestEntity);
		executor.execute(new HttpClientThread(httpclient, target, request, callback));
	}

	@Override
	public void close() throws IOException {
		executor.shutdown();
		super.close();
		try {
			executor.awaitTermination(30000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public RestClientResponse request(Method method, String requestUri, Map<String, String> headers,
			HttpEntity requestEntity) throws IOException {
		// TODO Auto-generated method stub
		return super.request(method, requestUri, headers, requestEntity);
	}

}
