package ac.jejunu.photify.rest;

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
	private static final String URL = ServerIpAddress.IP + "/writeArticle.photo";

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
			formData.add("fbid", command.getFbid());
			formData.add("content", command.getContent());
			formData.add("lat", "" + command.getLat());
			formData.add("lng", "" + command.getLng());
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
			Log.e(TAG, "$$$$$$$$$$$$$$$$$$  PostMessageTask - result : " + result);
		}
	}
}
