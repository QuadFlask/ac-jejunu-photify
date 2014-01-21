package ac.jejunu.photify.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class PostRequestSender {
	private static final String TAG = "PostRequestSender";

	public void postData(String url, String fbid) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			Log.e(TAG, "############ postData ###########");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("fbid", fbid));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			Log.e(TAG, "post execute post : " + httppost.toString());
			HttpResponse response = httpclient.execute(httppost);

			Log.e(TAG, "post response entity : " + response.getEntity().toString());
			for (org.apache.http.Header h : response.getAllHeaders())
				Log.e(TAG, "post response header : " + h.toString());
			Log.e(TAG, "post response entity content(encoding) : " + response.getEntity().getContentEncoding());
			Log.e(TAG, "post response entity content(type)     : " + response.getEntity().getContentType());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			Log.e(TAG, "post response json : " + json);
			Log.e(TAG, "*********** postData ***********");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getData(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httppost = new HttpGet(url);

		try {
			Log.e(TAG, "############ getData ###########");
			HttpResponse response = httpclient.execute(httppost);

			Log.e(TAG, "get response entity : " + response.getEntity().toString());
			for (org.apache.http.Header h : response.getAllHeaders())
				Log.e(TAG, "get response header : " + h.toString());
			Log.e(TAG, "get response entity content(encoding) : " + response.getEntity().getContentEncoding());
			Log.e(TAG, "get response entity content(type)     : " + response.getEntity().getContentType());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			Log.e(TAG, "post response json : " + json);
			Log.e(TAG, "************ getData ***********");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
