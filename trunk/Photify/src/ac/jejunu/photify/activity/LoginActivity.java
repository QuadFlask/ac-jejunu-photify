package ac.jejunu.photify.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import ac.jejunu.photify.R;
import ac.jejunu.photify.activity.fragment.FacebookLoginFragment;

public class LoginActivity extends FragmentActivity {
	private FacebookLoginFragment mainFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new FacebookLoginFragment();
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
		return true;
	}
}
