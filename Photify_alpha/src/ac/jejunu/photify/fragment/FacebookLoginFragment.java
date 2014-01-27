package ac.jejunu.photify.fragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ac.jejunu.photify.MainActivity;
import ac.jejunu.photify.R;
import ac.jejunu.photify.rest.ReadFacebookArticleClient;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EFragment(R.layout.fragment_facebook_login)
public class FacebookLoginFragment extends Fragment {
	@ViewById(R.id.authButton)
	LoginButton authButton;
	
	@RestService
	ReadFacebookArticleClient readArticleClient;
	
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	
	private boolean pendingPublishReauthorization = false;
	
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	@AfterViews
	void afterViews() {
		authButton.setFragment(this);// 반두시 필요
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(null);
		
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
			getPrefsEditor().putString("ACCESS_TOKEN", session.getAccessToken()).apply();
			goToMainActivity();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the fragment_facebook_login activity is launched
		// and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
		if (Session.getActiveSession().isOpened()) {
			// Request user data and show the results
			Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						// Log.e(TAG, "Response : " + response);
						// Log.e(TAG, "UserID : " + user.getId());
						// Log.e(TAG, "User FirstName : " +
						// user.getFirstName());
						getPrefsEditor().putString("FBID", user.getId()).apply();
						goToMainActivity();
					}
				}
			});
		}
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
			}
		} else if (state.isClosed()) {
		}
	}
	
	private void goToMainActivity() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
		getActivity().finish();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	private Editor getPrefsEditor() {
		return getActivity().getSharedPreferences("PHOTIFY", Activity.MODE_PRIVATE).edit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}
	
	public void printHashKey() {
		try {
			PackageInfo info = getActivity().getPackageManager().getPackageInfo("ac.jejunu.photify", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("TEMPTAGHASH KEY:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
