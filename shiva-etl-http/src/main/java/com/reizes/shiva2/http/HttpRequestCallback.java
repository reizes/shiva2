package com.reizes.shiva2.http;

import org.apache.http.HttpResponse;

public interface HttpRequestCallback {
	public void onHttpResponse(HttpResponse httpResponse);
}
