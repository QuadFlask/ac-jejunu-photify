package ac.jejunu.photify.activity.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.Article;
import ac.jejunu.photify.rest.ArticleCommandRestClient;
import ac.jejunu.photify.rest.MyRestClient;

@EFragment(R.layout.fragment_main)
public class JsonTestFragment extends Fragment {

	@ViewById(R.id.section_label)
	TextView section_label;

	@RestService
	MyRestClient myRestClient;

	@RestService
	ArticleCommandRestClient articleCommandRestClient;

	@AfterViews
	void onBindComplete() {
		try {
			backgroundJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@UiThread
	void appendData(String data) {
		String s = section_label.getText().toString();
		section_label.setText(s.concat(data));
	}

	int i = 100;

	@Background
	void backgroundJob() {
		try {
			int no = 2015 + i++;

			Article article = new Article();
			article.setArticleid(no);
			article.setTitle("article title " + no);
			article.setContents("article contents");
			article.setFbid("10010101010");
			article.setLat(123);
			article.setLng(456);
			article.setPhotoUrl("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c12.12.176.176/374637_189230964497111_247316888_n.jpg");
			article.setWriter("article writer Flask");
			appendData("writing, " + no);
			appendData(" : " + article.toString());

			postTest("http://192.168.0.3:8080/writearticle.photo", article);

//			String result = articleCommandRestClient.writeArticle(article);
//			appendData(" // write result = " + result);
//			List<Article> articles = articleCommandRestClient.getArticles(new ArticleCommand(2, 2));
//
//			for (Article a : articles) {
//				appendData(a.toString());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String post(String url, Article article) {
		InputStream inputStream;
		String result = "";
		try {
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 3. build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("articleid", article.getArticleid());
			jsonObject.accumulate("title", article.getTitle());
			jsonObject.accumulate("photoUrl", article.getPhotoUrl());

			// 4. convert JSONObject to JSON to String
			json = jsonObject.toString();

			// ** Alternative way to convert Person object to JSON string usin Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("InputStream", e.getLocalizedMessage());
		}

		// 11. return result
		return result;
	}

	private String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	Gson gson = new Gson();

	void postTest(String url, Article article) {
		try {
			HttpClient httpClient = new DefaultHttpClient();

			HttpPost postRequest = new HttpPost(url);

			StringEntity se = new StringEntity(gson.toJson(article));
			postRequest.setEntity(se);
//			postRequest.setHeader("Accept", "application/json");
//			postRequest.setHeader("Content-type", "application/json");

			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}