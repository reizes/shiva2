package com.reizes.shiva2.http.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.reizes.shiva2.http.Method;

public class Utils {
	
	public static HttpUriRequest buildRequest(Method method, String requestUri, Map<String, String> headers, HttpEntity requestEntity) {
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
		
		if (headers != null) {
			for (String key : headers.keySet()) {
				requestBase.addHeader(key, headers.get(key));
			}
		}
		
		return requestBase;
	}
	
	public static  StringEntity getStringEntity(boolean chunked, String body, String contentType) {
		StringEntity entity = new StringEntity(body, Consts.UTF_8);
		entity.setChunked(chunked);
		entity.setContentType(contentType);
		return entity;
	}
	
	public static  StringEntity getStringEntity(boolean chunked, String body) {
		return getStringEntity(chunked, body, "text/plain");
	}
	
	public static  StringEntity getStringEntity(String body) {
		return getStringEntity(true, body, "text/plain");
	}
	
	public static  StringEntity getStringEntity(String body, String contentType) {
		return getStringEntity(true, body, contentType);
	}
	
	public static List<NameValuePair> getNameValuePairListFromMap(Map<String, String> map) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(String key : map.keySet()) {
			list.add(new BasicNameValuePair(key, map.get(key)));
		}
		
		return list;
	}

}
