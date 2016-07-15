package com.reizes.shiva2.http;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.reizes.shiva2.core.ExceptionListener;
import com.reizes.shiva2.http.inner.Utils;

import lombok.Getter;
import lombok.Setter;

public class ThreadedRestClient implements Closeable {
	private static ThreadedRestClient instance = null;
	private Gson gson = new Gson();
	private CloseableHttpClient httpclient;

	@Getter
	@Setter
	private ExceptionListener exceptionListener;
	
	private ThreadedRestClient() {
		httpclient = ThreadedRestClientPool.getInstance().getHttpclient();
	}
	
	public synchronized static ThreadedRestClient getInstance() {
		if (instance==null) {
			instance = new ThreadedRestClient();
		}
		
		return instance;
	}

	public void requestAsync(URI uri, Method method, String requestUri, Map<String, String> headers, HttpEntity requestEntity, HttpRequestCallback callback) throws IOException {
		if (!ThreadedRestClientPool.getInstance().isShutdown()) {
			HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
			HttpUriRequest request = Utils.buildRequest(method, requestUri, headers, requestEntity);
			
			try {
				CloseableHttpResponse httpResponse = httpclient.execute(target, request);
				try {
					if (callback!=null) {
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
	}
	
	public void requestAsync(String uri, Method method, String requestUri, Map<String, String> headers, HttpEntity requestEntity, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(new URI(uri), method, requestUri, headers, requestEntity, callback);
	}

	@Override
	public synchronized void close() throws IOException {
		if (!ThreadedRestClientPool.getInstance().isShutdown()) {
			ThreadedRestClientPool.getInstance().close();
		}
	}
	
	public boolean isClosed() {
		return ThreadedRestClientPool.getInstance().isShutdown();
	}
 
	public void get(String server, String requestUri, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.GET, requestUri, null, null, callback);
	}

	public void get(String server, String requestUri, Map<String, String> headers, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.GET, requestUri, headers, null, callback);
	}

	public void delete(String server, String requestUri, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.DELETE, requestUri, null, null, callback);
	}

	public void delete(String server, String requestUri, Map<String, String> headers, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.DELETE, requestUri, headers, null, callback);
	}

	public void post(String server, String requestUri, HttpEntity entity, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, null, entity, callback);
	}

	public void post(String server, String requestUri, Map<String, String> headers, HttpEntity entity, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, entity, callback);
	}

	public void postString(String server, String requestUri, Map<String, String> headers, String body, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, Utils.getStringEntity(body), callback);
	}

	public void postString(String server, String requestUri, Map<String, String> headers, String body, String contentType, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, Utils.getStringEntity(body, contentType), callback);
	}

	public void postString(String server, String requestUri, Map<String, String> headers, String body, boolean chunked, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, Utils.getStringEntity(chunked, body), callback);
	}

	public void postString(String server, String requestUri, Map<String, String> headers, String body, boolean chunked, String contentType, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, Utils.getStringEntity(chunked, body, contentType), callback);
	}

	public void postString(String server, String requestUri, String body, HttpRequestCallback callback) throws IOException, URISyntaxException {
		postString(server, requestUri, null, body, callback);
	}

	public void postString(String server, String requestUri, String body, boolean chunked, HttpRequestCallback callback) throws IOException, URISyntaxException {
		postString(server, requestUri, null, body, chunked, callback);
	}

	public void postJsonString(String server, String requestUri, String jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		postString(server, requestUri, null, gson.toJson(jsonData), "application/json", callback);
	}

	public void postJsonString(String server, String requestUri, Map<String, String> headers, String jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		postString(server, requestUri, headers, gson.toJson(jsonData), "application/json", callback);
	}

	public void postJson(String server, String requestUri, Map<String, Object> jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		postJsonString(server, requestUri,gson.toJson(jsonData), callback);
	}

	public void postJson(String server, String requestUri, Map<String, String> headers, Map<String, Object> jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		postJsonString(server, requestUri, headers, gson.toJson(jsonData), callback);
	}

	public void postFormParams(String server, String requestUri, Map<String, String> params, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, null, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8), callback);
	}

	public void postFormParams(String server, String requestUri, Map<String, String> headers, Map<String, String> params, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8), callback);
	}

	public void postFile(String server, String requestUri, File file, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, null, new BufferedHttpEntity(new FileEntity(file)), callback);
	}

	public void postFile(String server, String requestUri, Map<String, String> headers, File file, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, new BufferedHttpEntity(new FileEntity(file)), callback);
	}

	public void postInputStream(String server, String requestUri, InputStream is, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, null, new BufferedHttpEntity(new InputStreamEntity(is)), callback);
	}

	public void postInputStream(String server, String requestUri, Map<String, String> headers, InputStream is, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.POST, requestUri, headers, new BufferedHttpEntity(new InputStreamEntity(is)), callback);
	}

	public void put(String server, String requestUri, HttpEntity entity, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, null, entity, callback);
	}

	public void put(String server, String requestUri, Map<String, String> headers, HttpEntity entity, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, entity, callback);
	}

	public void putString(String server, String requestUri, Map<String, String> headers, String body, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, Utils.getStringEntity(body), callback);
	}

	public void putString(String server, String requestUri, Map<String, String> headers, String body, String contentType, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, Utils.getStringEntity(body, contentType), callback);
	}

	public void putString(String server, String requestUri, Map<String, String> headers, String body, boolean chunked, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, Utils.getStringEntity(chunked, body), callback);
	}

	public void putString(String server, String requestUri, Map<String, String> headers, String body, boolean chunked, String contentType, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, Utils.getStringEntity(chunked, body, contentType), callback);
	}

	public void putString(String server, String requestUri, String body, HttpRequestCallback callback) throws IOException, URISyntaxException {
		putString(server, requestUri, null, body, callback);
	}

	public void putString(String server, String requestUri, String body, boolean chunked, HttpRequestCallback callback) throws IOException, URISyntaxException {
		putString(server, requestUri, null, body, chunked, callback);
	}

	public void putJsonString(String server, String requestUri, String jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		putString(server, requestUri, null, jsonData, "application/json", callback);
	}

	public void putJsonString(String server, String requestUri, Map<String, String> headers, String jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		putString(server, requestUri, headers, jsonData, "application/json", callback);
	}

	public void putJson(String server, String requestUri, Map<String, Object> jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		putJsonString(server, requestUri, gson.toJson(jsonData), callback);
	}

	public void putJson(String server, String requestUri, Map<String, String> headers, Map<String, Object> jsonData, HttpRequestCallback callback) throws IOException, URISyntaxException {
		putJsonString(server, requestUri, headers, gson.toJson(jsonData), callback);
	}

	public void putFormParams(String server, String requestUri, Map<String, String> params, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, null, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8), callback);
	}

	public void putFormParams(String server, String requestUri, Map<String, String> headers, Map<String, String> params, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8), callback);
	}

	public void putFile(String server, String requestUri, File file, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, null, new BufferedHttpEntity(new FileEntity(file)), callback);
	}

	public void putFile(String server, String requestUri, Map<String, String> headers, File file, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, new BufferedHttpEntity(new FileEntity(file)), callback);
	}

	public void putInputStream(String server, String requestUri, InputStream is, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, null, new BufferedHttpEntity(new InputStreamEntity(is)), callback);
	}

	public void putInputStream(String server, String requestUri, Map<String, String> headers, InputStream is, HttpRequestCallback callback) throws IOException, URISyntaxException {
		requestAsync(server, Method.PUT, requestUri, headers, new BufferedHttpEntity(new InputStreamEntity(is)), callback);
	}
}
