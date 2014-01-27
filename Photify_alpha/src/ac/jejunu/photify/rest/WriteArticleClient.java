package ac.jejunu.photify.rest;

import java.net.URLEncoder;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ac.jejunu.photify.entity.ArticleCommand;
import android.os.AsyncTask;
import android.util.Log;

public class WriteArticleClient {
	private static final String TAG = "RestTemplateController";
	private static final String URL = ServerIpAddress.IP + "/writeArticle.photo";
	
	public void write(ArticleCommand command, String accessToken, OnUploadCompletedCallback callback) {
		new PostMessageTask(command, accessToken, callback).execute();
	}
	
	public interface OnUploadCompletedCallback {
		public void onUploadCompleted(String result);
	}
	
	private class PostMessageTask extends AsyncTask<Void, Void, String> {
		private MultiValueMap<String, Object> formData;
		private ArticleCommand command;
		private String accessToken;
		private OnUploadCompletedCallback callback;
		
		public PostMessageTask(ArticleCommand command, String accessToken, OnUploadCompletedCallback callback) {
			this.command = command;
			this.accessToken = accessToken;
			this.callback = callback;
		}
		
		@Override
		protected void onPreExecute() {
			try {
			formData = new LinkedMultiValueMap<String, Object>();
			formData.add("fbid", command.getFbid());
			formData.add("content",  URLEncoder.encode(command.getContent(),"UTF-8"));
			formData.add("lat", "" + command.getLat());
			formData.add("lng", "" + command.getLng());
			formData.add("accesstoken", accessToken);
			formData.add("attach", new FileSystemResource(command.getAttachPath()));
			}catch(Exception e) {}
		}
		
		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpHeaders requestHeaders = new HttpHeaders();
				
				// Sending multipart/form-data
				requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
				requestHeaders.set("Connection", "Close");
				
				// Populate the MultiValueMap being serialized and headers in an
				// HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData, requestHeaders);
				
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate(true);
				restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
				
				// Make the network request, posting the message, expecting a
				// String in response from the server
				ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
				
				// Return the response body to display to the user
				return response.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			executeCallback(result);
//			Log.e(TAG, "$$$$$$$$$$$$$$$$$$  PostMessageTask - result : " + result);
		}
		
		@Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
			executeCallback(null); // failed.
		}
		
		private void executeCallback(String result) {
			if (callback != null) callback.onUploadCompleted(result);
		}
	}
}
