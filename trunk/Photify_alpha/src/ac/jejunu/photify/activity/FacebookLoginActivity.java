package ac.jejunu.photify.activity;

import ac.jejunu.photify.fragment.FacebookLoginFragment;
import ac.jejunu.photify.fragment.FacebookLoginFragment_;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FacebookLoginActivity extends FragmentActivity {
	private FacebookLoginFragment mainFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		getActionBar().hide();
		
		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new FacebookLoginFragment_();
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit();
		} else {
			// Or set the fragment from restored state info
			mainFragment = (FacebookLoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
		}
	}
}
