package ac.jejunu.photify.rest;

import java.io.InputStream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.util.Log;

public class RestTemplateController {
	private static final String TAG = "RestTemplateController";

	private class PostMessageTask extends AsyncTask<Void, Void, String> {

		private MultiValueMap<String, Object> formData;

		// Resource resource = new
		// ClassPathResource("res/drawable/spring09_logo.png");
		private InputStream getData() {
			return null;
		}

		@Override
		protected void onPreExecute() {
			formData = new LinkedMultiValueMap<String, Object>();
			formData.add("description", "Spring logo");
			formData.add("file", getData());
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				// The URL for making the POST request
				final String url = "";

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

		}

	}

}
