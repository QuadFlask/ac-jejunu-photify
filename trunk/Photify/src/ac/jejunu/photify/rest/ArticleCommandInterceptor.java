package ac.jejunu.photify.rest;

import android.util.Log;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

class ArticleCommandInterceptor implements ClientHttpRequestInterceptor {
	public ArticleCommandInterceptor() {
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
		Log.e("ArticleCommandInterceptor", "############### ArticleCommandInterceptor intercept()");
		Log.e("ArticleCommandInterceptor", "method : " + httpRequest.getMethod());
		Log.e("ArticleCommandInterceptor", "uri : " + httpRequest.getURI());
		Log.e("ArticleCommandInterceptor", "headers : " + httpRequest.getHeaders());
		Log.e("ArticleCommandInterceptor", "toString" + httpRequest.toString());
		Log.e("ArticleCommandInterceptor", "bytes : " + new String(bytes));
		Log.e("ArticleCommandInterceptor", "############### ArticleCommandInterceptor intercept()");
		return clientHttpRequestExecution.execute(httpRequest, bytes);
	}
}