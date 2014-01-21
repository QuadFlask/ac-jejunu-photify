package ac.jejunu.photify.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class DefaultRestController {
	private static final String TAG = "DefaultRestController";
	private HttpClient httpClient;

	public DefaultRestController() {
		httpClient = new DefaultHttpClient();
	}

	public String post(String url, String name, String value) throws ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(name, value));

		return post(url, new UrlEncodedFormEntity(nameValuePairs));
	}

	public String post(String url, HttpEntity entity) throws UnsupportedEncodingException, IllegalStateException, IOException {
		HttpPost httpPost = new HttpPost(url);

		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);

		return getBodyFromResponse(response);
	}

	public String get(String url) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpClient.execute(httpGet);

		return getBodyFromResponse(response);
	}

	private String getBodyFromResponse(HttpResponse response) throws UnsupportedEncodingException, IllegalStateException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String result = reader.readLine();
		reader.close();
		return result;
	}

}
