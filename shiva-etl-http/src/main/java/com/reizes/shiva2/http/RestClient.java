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
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.reizes.shiva2.http.inner.Utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RestClient implements Closeable {
	private URI uri;
	private Gson gson = new Gson();
	private boolean chunked = true;
	protected CloseableHttpClient httpclient;

	public RestClient(URI uri) {
		this.setUri(uri);
		httpclient = initHttpClient();
	}

	public RestClient(String uri) throws URISyntaxException {
		this.setUri(new URI(uri));
		httpclient = initHttpClient();
	}
	
	protected CloseableHttpClient initHttpClient() {
		return HttpClients.createDefault();
	}

	public RestClientResponse request(Method method, String requestUri, Map<String, String> headers, HttpEntity requestEntity)
			throws IOException {
		try {
			// specify the host, protocol, and port
			HttpHost target = new HttpHost(this.uri.getHost(), this.uri.getPort(), this.uri.getScheme());
			HttpUriRequest request = Utils.buildRequest(method, requestUri, headers, requestEntity);
			
			log.debug("executing request to " + target + requestUri);
			
			HttpResponse httpResponse = httpclient.execute(target, request);
			RestClientResponse response = RestClientResponse.fromHttpResponse(httpResponse);
			EntityUtils.consume(httpResponse.getEntity());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return null;
	}

	public RestClientResponse get(String requestUri) throws IOException {
		return request(Method.GET, requestUri, null, null);
	}

	public RestClientResponse get(String requestUri, Map<String, String> headers) throws IOException {
		return request(Method.GET, requestUri, headers, null);
	}

	public RestClientResponse delete(String requestUri) throws IOException {
		return request(Method.DELETE, requestUri, null, null);
	}

	public RestClientResponse delete(String requestUri, Map<String, String> headers) throws IOException {
		return request(Method.DELETE, requestUri, headers, null);
	}

	public RestClientResponse post(String requestUri, HttpEntity entity) throws IOException {
		return request(Method.POST, requestUri, null, entity);
	}

	public RestClientResponse post(String requestUri, Map<String, String> headers, HttpEntity entity) throws IOException {
		return request(Method.POST, requestUri, headers, entity);
	}

	public RestClientResponse postString(String requestUri, Map<String, String> headers, String body) throws IOException {
		return request(Method.POST, requestUri, headers, Utils.getStringEntity(chunked, body));
	}

	public RestClientResponse postString(String requestUri, Map<String, String> headers, String body, String contentType) throws IOException {
		return request(Method.POST, requestUri, headers, Utils.getStringEntity(chunked, body, contentType));
	}

	public RestClientResponse postString(String requestUri, String body) throws IOException {
		return postString(requestUri, null, body);
	}

	public RestClientResponse postJsonString(String requestUri, String jsonData) throws IOException {
		return postString(requestUri, null, gson.toJson(jsonData), "application/json");
	}

	public RestClientResponse postJsonString(String requestUri, Map<String, String> headers, String jsonData) throws IOException {
		return postString(requestUri, headers, gson.toJson(jsonData), "application/json");
	}

	public RestClientResponse postJson(String requestUri, Map<String, Object> jsonData) throws IOException {
		return postJsonString(requestUri,gson.toJson(jsonData));
	}

	public RestClientResponse postJson(String requestUri, Map<String, String> headers, Map<String, Object> jsonData) throws IOException {
		return postJsonString(requestUri, headers, gson.toJson(jsonData));
	}

	public RestClientResponse postFormParams(String requestUri, Map<String, String> params) throws IOException {
		return request(Method.POST, requestUri, null, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public RestClientResponse postFormParams(String requestUri, Map<String, String> headers, Map<String, String> params) throws IOException {
		return request(Method.POST, requestUri, headers, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public RestClientResponse postFile(String requestUri, File file) throws IOException {
		return request(Method.POST, requestUri, null, new BufferedHttpEntity(new FileEntity(file)));
	}

	public RestClientResponse postFile(String requestUri, Map<String, String> headers, File file) throws IOException {
		return request(Method.POST, requestUri, headers, new BufferedHttpEntity(new FileEntity(file)));
	}

	public RestClientResponse postInputStream(String requestUri, InputStream is) throws IOException {
		return request(Method.POST, requestUri, null, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	public RestClientResponse postInputStream(String requestUri, Map<String, String> headers, InputStream is) throws IOException {
		return request(Method.POST, requestUri, headers, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	public RestClientResponse put(String requestUri, HttpEntity entity) throws IOException {
		return request(Method.PUT, requestUri, null, entity);
	}

	public RestClientResponse put(String requestUri, Map<String, String> headers, HttpEntity entity) throws IOException {
		return request(Method.PUT, requestUri, headers, entity);
	}

	public RestClientResponse putString(String requestUri, Map<String, String> headers, String body) throws IOException {
		return request(Method.PUT, requestUri, headers, Utils.getStringEntity(chunked, body));
	}

	public RestClientResponse putString(String requestUri, Map<String, String> headers, String body, String contentType) throws IOException {
		return request(Method.PUT, requestUri, headers, Utils.getStringEntity(chunked, body, contentType));
	}

	public RestClientResponse putString(String requestUri, String body) throws IOException {
		return putString(requestUri, null, body);
	}

	public RestClientResponse putJsonString(String requestUri, String jsonData) throws IOException {
		return putString(requestUri, null, jsonData, "application/json");
	}

	public RestClientResponse putJsonString(String requestUri, Map<String, String> headers, String jsonData) throws IOException {
		return putString(requestUri, headers, jsonData, "application/json");
	}

	public RestClientResponse putJson(String requestUri, Map<String, Object> jsonData) throws IOException {
		return putJsonString(requestUri, gson.toJson(jsonData));
	}

	public RestClientResponse putJson(String requestUri, Map<String, String> headers, Map<String, Object> jsonData) throws IOException {
		return putJsonString(requestUri, headers, gson.toJson(jsonData));
	}

	public RestClientResponse putFormParams(String requestUri, Map<String, String> params) throws IOException {
		return request(Method.PUT, requestUri, null, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public RestClientResponse putFormParams(String requestUri, Map<String, String> headers, Map<String, String> params) throws IOException {
		return request(Method.PUT, requestUri, headers, new UrlEncodedFormEntity(Utils.getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public RestClientResponse putFile(String requestUri, File file) throws IOException {
		return request(Method.PUT, requestUri, null, new BufferedHttpEntity(new FileEntity(file)));
	}

	public RestClientResponse putFile(String requestUri, Map<String, String> headers, File file) throws IOException {
		return request(Method.PUT, requestUri, headers, new BufferedHttpEntity(new FileEntity(file)));
	}

	public RestClientResponse putInputStream(String requestUri, InputStream is) throws IOException {
		return request(Method.PUT, requestUri, null, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	public RestClientResponse putInputStream(String requestUri, Map<String, String> headers, InputStream is) throws IOException {
		return request(Method.PUT, requestUri, headers, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	@Override
	public void close() throws IOException {
		if (httpclient != null) {
			httpclient.close();
		}
	}

}
