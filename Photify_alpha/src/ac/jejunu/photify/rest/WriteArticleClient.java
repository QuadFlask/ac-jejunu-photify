package ac.jejunu.photify.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ac.jejunu.photify.entity.ArticleCommand;
import android.os.AsyncTask;
import android.util.Log;

public class WriteArticleClient {
	private static final String TAG = "RestTemplateController";
	private static final String url = "http://113.198.164.111:8080/writeArticle.photo";

	public void write(ArticleCommand command) {
		new PostMessageTask(command).execute();
	}

	private class PostMessageTask extends AsyncTask<Void, Void, String> {

		private MultiValueMap<String, Object> formData;
		private ArticleCommand command;

		public PostMessageTask(ArticleCommand command) {
			this.command = command;
		}

		@Override
		protected void onPreExecute() {
			formData = new LinkedMultiValueMap<String, Object>();
			formData.add("content", command.getContent());
			formData.add("fbid", command.getFbid());
			formData.add("lat", "" + command.getLat());
			formData.add("lng", "" + command.getLng());
			// formData.add("file", getData());
			ClassPathResource classPathResource = new ClassPathResource("res/drawable-hdpi/ic_launcher.png");
			if (!classPathResource.exists()) {
				Log.e(TAG, "error on loading ClassPathResources");
			}
			formData.add("attach", new FileSystemResource(command.getAttachPath()));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpHeaders requestHeaders = new HttpHeaders();

				// Sending multipart/form-data
				requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

				// Populate the MultiValueMap being serialized and headers in an
				// HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData, requestHeaders);

				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate(true);

				// Make the network request, posting the message, expecting a
				// String in response from the server
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

				// Return the response body to display to the user
				return response.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.e(TAG, "$$$$$$$$$$$$$$$$$$  PostMessageTask - result : " + result);
		}

		private InputStream getData() {
			try {
				return new FileInputStream(command.getAttachPath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
