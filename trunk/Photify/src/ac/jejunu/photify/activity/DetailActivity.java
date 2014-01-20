package ac.jejunu.photify.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import ac.jejunu.photify.R;
import ac.jejunu.photify.view.UrlImageView;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

//		Intent intent = getIntent();
//		String postid = intent.getStringExtra("postid");
		// get json data from server??

		try {
			UrlImageView mainImage = (UrlImageView) findViewById(R.id.iv_mainphoto);
			mainImage.setImageURL(new URL("http://icon.daumcdn.net/w/icon/1312/19/152729032.png"));

			for (int i = 0; i < 10; i++)
				addComment("https://cdn2.iconfinder.com/data/icons/ios-7-icons/50/user_male2-256.png", "name" + i, "text" + i);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void addComment(String profilePicPath, String name, String contents) throws MalformedURLException {
		LinearLayout commentsLayout = (LinearLayout) findViewById(R.id.ll_comments);
		View child = getLayoutInflater().inflate(R.layout.profile_and_text, null);

		UrlImageView profilePic = (UrlImageView) child.findViewById(R.id.iv_profilepic);
		TextView tvName = (TextView) child.findViewById(R.id.tv_name);
		TextView tvContent = (TextView) child.findViewById(R.id.tv_contents);

		profilePic.setImageURL(new URL(profilePicPath));
		tvName.setText(name);
		tvContent.setText(contents);

		commentsLayout.addView(child);
	}

}
