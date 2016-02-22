package com.reizes.shiva2.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	URI uri;

	public enum Method {
		GET, PUT, POST, DELETE;
	};

	public RestClient(URI uri) {
		this.setUri(uri);
	}

	public RestClient(String uri) throws URISyntaxException {
		this.setUri(new URI(uri));
	}

	public InputStream request(Method method, String requestUri, Map<String, String> headers, HttpEntity requestEntity)
			throws IOException {
		InputStream is = null;
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

			log.debug(httpResponse.getStatusLine().toString());
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

		return is;
	}

	public InputStream get(String requestUri) throws IOException {
		return request(Method.GET, requestUri, null, null);
	}

	public InputStream get(String requestUri, Map<String, String> headers) throws IOException {
		return request(Method.GET, requestUri, headers, null);
	}

	public InputStream delete(String requestUri) throws IOException {
		return request(Method.DELETE, requestUri, null, null);
	}

	public InputStream delete(String requestUri, Map<String, String> headers) throws IOException {
		return request(Method.DELETE, requestUri, headers, null);
	}

	public InputStream post(String requestUri, HttpEntity entity) throws IOException {
		return request(Method.POST, requestUri, null, entity);
	}

	public InputStream post(String requestUri, Map<String, String> headers, HttpEntity entity) throws IOException {
		return request(Method.POST, requestUri, headers, entity);
	}

	public InputStream postString(String requestUri, String body) throws IOException {
		return request(Method.POST, requestUri, null, new StringEntity(body));
	}

	public InputStream postString(String requestUri, Map<String, String> headers, String body) throws IOException {
		return request(Method.POST, requestUri, headers, new StringEntity(body));
	}

	public InputStream postFormParams(String requestUri, Map<String, String> params) throws IOException {
		return request(Method.POST, requestUri, null, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public InputStream postFormParams(String requestUri, Map<String, String> headers, Map<String, String> params) throws IOException {
		return request(Method.POST, requestUri, headers, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public InputStream postFile(String requestUri, File file) throws IOException {
		return request(Method.POST, requestUri, null, new BufferedHttpEntity(new FileEntity(file)));
	}

	public InputStream postFile(String requestUri, Map<String, String> headers, File file) throws IOException {
		return request(Method.POST, requestUri, headers, new BufferedHttpEntity(new FileEntity(file)));
	}

	public InputStream postInputStream(String requestUri, InputStream is) throws IOException {
		return request(Method.POST, requestUri, null, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	public InputStream postInputStream(String requestUri, Map<String, String> headers, InputStream is) throws IOException {
		return request(Method.POST, requestUri, headers, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	public InputStream put(String requestUri, HttpEntity entity) throws IOException {
		return request(Method.PUT, requestUri, null, entity);
	}

	public InputStream put(String requestUri, Map<String, String> headers, HttpEntity entity) throws IOException {
		return request(Method.PUT, requestUri, headers, entity);
	}

	public InputStream putString(String requestUri, String body) throws IOException {
		return request(Method.PUT, requestUri, null, new StringEntity(body));
	}

	public InputStream putString(String requestUri, Map<String, String> headers, String body) throws IOException {
		return request(Method.PUT, requestUri, headers, new StringEntity(body));
	}

	public InputStream putFormParams(String requestUri, Map<String, String> params) throws IOException {
		return request(Method.PUT, requestUri, null, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public InputStream putFormParams(String requestUri, Map<String, String> headers, Map<String, String> params) throws IOException {
		return request(Method.PUT, requestUri, headers, new UrlEncodedFormEntity(getNameValuePairListFromMap(params), Consts.UTF_8));
	}

	public InputStream putFile(String requestUri, File file) throws IOException {
		return request(Method.PUT, requestUri, null, new BufferedHttpEntity(new FileEntity(file)));
	}

	public InputStream putFile(String requestUri, Map<String, String> headers, File file) throws IOException {
		return request(Method.PUT, requestUri, headers, new BufferedHttpEntity(new FileEntity(file)));
	}

	public InputStream putInputStream(String requestUri, InputStream is) throws IOException {
		return request(Method.PUT, requestUri, null, new BufferedHttpEntity(new InputStreamEntity(is)));
	}

	public InputStream putInputStream(String requestUri, Map<String, String> headers, InputStream is) throws IOException {
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
