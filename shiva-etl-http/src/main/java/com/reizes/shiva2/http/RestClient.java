package com.reizes.shiva2.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RestClient {
	private URI uri;
	private Gson gson = new Gson();
	private boolean chunked = true;

	public enum Method {
		GET, PUT, POST, DELETE;
	};

	public RestClient(URI uri) {
		this.setUri(uri);
	}

	public RestClient(String uri) throws URISyntaxException {
		this.setUri(new URI(uri));
	}

	public RestClientResponse request(Method method, String requestUri, Map<String, String> headers, HttpEntity requestEntity)
			throws IOException {
		InputStream is = null;
		RestClientResponse restClientResponse = new RestClientResponse();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// specify the host, protocol, and port
			HttpHost target = new HttpHost(this.uri.getHost(), this.uri.getPort(), this.uri.getScheme());

			HttpRequestBase requestBase = null;
			switch (method) {
			case PUT:
				requestBase = new HttpPut(requestUri);
				if (requestEntity != null) {
					((HttpPut) requestBase).setEntity(requestEntity);
				}
				break;
			case POST:
				requestBase = new HttpPost(requestUri);
				if (requestEntity != null) {
					((HttpPost) requestBase).setEntity(requestEntity);
				}
				break;
			case DELETE:
				requestBase = new HttpDelete(requestUri);
				break;
			case GET:
			default:
				requestBase = new HttpGet(requestUri);
				break;
			}
			
			log.debug("executing request to " + target + requestUri);

			if (headers != null) {
				for (String key : headers.keySet()) {
					requestBase.addHeader(key, headers.get(key));
				}
			}
			
			HttpResponse httpResponse = httpclient.execute(target, requestBase);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);
				is = entity.getContent();
			}

			StatusLine statusLine = httpResponse.getStatusLine();
			restClientResponse.setResponseCode(statusLine.getStatusCode());
			restClientResponse.setStatusText(statusLine.toString());
			if (statusLine.getStatusCode() != 200 && statusLine.getStatusCode() != 204) {
				log.error(requestUri+" "+method.name()+"\t"+statusLine.toString()+"\t"+requestEntity.toString());
			}
			Header[] responseHeaders = httpResponse.getAllHeaders();
			for (int i = 0; i < responseHeaders.length; i++) {
				log.debug(responseHeaders[i].toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.close();
		}
		
		restClientResponse.setResponse(is);

		return restClientResponse;
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
		StringEntity entity = new StringEntity(body, Consts.UTF_8);
		entity.setChunked(chunked);
		return request(Method.POST, requestUri, headers, entity);
	}

	public RestClientResponse postString(String requestUri, Map<String, String> headers, String body, String contentType) throws IOException {
		StringEntity entity = new StringEntity(body, Consts.UTF_8);
		entity.setChunked(chunked);
		entity.setContentType(contentType);
		return request(Method.POST, requestUri, headers, entity);
	}

	public RestClientResponse postString(String requestUri, String body) throws IOException {
		return postString(requestUri, null, body);
	}

	public RestClientResponse postJsonString(String requestUri, String jsonData) throws IOException {
		return postString(requestUri, null, gson.toJson(jsonData), "Application/json");
	}

	public RestClientResponse postJsonString(String requestUri, Map<String, String> headers, String jsonData) throws IOException {
		return postString(requestUri, headers, gson.toJson(jsonData), "Application/json");
	}

	public RestClientResponse postJson(String requestUri, Map<String, Object> jsonData) throws IOException {
		return postJsonString(requestUri,gson.toJson(jsonData));
	}

	public RestClientResponse postJson(String requestUri, Map<String, String> headers, Map<String, Object> jsonData) throws IOException {
		return postJsonString(requestUri, headers, gson.toJson(jsonData));
	}

	public RestClientResponse postFormParams(String requestUri, Map<String, String> params) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		return request(Method.POST, requestUri, headers, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public RestClientResponse postFormParams(String requestUri, Map<String, String> headers, Map<String, String> params) throws IOException {
		if (headers!=null && !headers.containsKey("Content-Type")) {
			headers.put("Content-Type", "application/x-www-form-urlencoded");
		}
		return request(Method.POST, requestUri, headers, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
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
		StringEntity entity = new StringEntity(body, Consts.UTF_8);
		entity.setChunked(chunked);
		return request(Method.PUT, requestUri, headers, entity);
	}

	public RestClientResponse putString(String requestUri, Map<String, String> headers, String body, String contentType) throws IOException {
		StringEntity entity = new StringEntity(body, Consts.UTF_8);
		entity.setChunked(chunked);
		entity.setContentType(contentType);
		return request(Method.PUT, requestUri, headers, entity);
	}

	public RestClientResponse putString(String requestUri, String body) throws IOException {
		return putString(requestUri, null, body);
	}

	public RestClientResponse putJsonString(String requestUri, String jsonData) throws IOException {
		return putString(requestUri, null, jsonData, "Application/json");
	}

	public RestClientResponse putJsonString(String requestUri, Map<String, String> headers, String jsonData) throws IOException {
		return putString(requestUri, headers, jsonData, "Application/json");
	}

	public RestClientResponse putJson(String requestUri, Map<String, Object> jsonData) throws IOException {
		return putJsonString(requestUri, gson.toJson(jsonData));
	}

	public RestClientResponse putJson(String requestUri, Map<String, String> headers, Map<String, Object> jsonData) throws IOException {
		return putJsonString(requestUri, headers, gson.toJson(jsonData));
	}

	public RestClientResponse putFormParams(String requestUri, Map<String, String> params) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		return request(Method.PUT, requestUri, headers, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public RestClientResponse putFormParams(String requestUri, Map<String, String> headers, Map<String, String> params) throws IOException {
		if (headers!=null && !headers.containsKey("Content-Type")) {
			headers.put("Content-Type", "application/x-www-form-urlencoded");
		}
		return request(Method.PUT, requestUri, headers, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
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
	
	private List<NameValuePair> getNameValuePairListFromMap(Map<String, String> map) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(String key : map.keySet()) {
			list.add(new BasicNameValuePair(key, map.get(key)));
		}
		
		return list;
	}

}
