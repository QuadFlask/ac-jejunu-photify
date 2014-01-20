package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import ac.jejunu.photify.fragment.FacebookLoginFragment;
import ac.jejunu.photify.fragment.FacebookLoginFragment_;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class FacebookLoginActivity extends FragmentActivity {
	private FacebookLoginFragment mainFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new FacebookLoginFragment_();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, mainFragment)
					.commit();
		} else {
			// Or set the fragment from restored state info
			mainFragment = (FacebookLoginFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
