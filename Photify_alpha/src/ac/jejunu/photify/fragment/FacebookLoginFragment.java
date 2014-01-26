package ac.jejunu.photify.fragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ac.jejunu.photify.R;
import ac.jejunu.photify.rest.DefaultRestClient;
import ac.jejunu.photify.rest.ReadFacebookArticleClient;
import ac.jejunu.photify.rest.ServerIpAddress;
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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EFragment(R.layout.fragment_facebook_login)
public class FacebookLoginFragment extends Fragment {
	private static final String TAG = "FacebookLoginFragment";
	
	@ViewById(R.id.shareButton)
	Button shareButton;
	
	@ViewById(R.id.sendRequestButton)
	Button sendRequestButton;
	
	@ViewById(R.id.authButton)
	LoginButton authButton;
	
	@RestService
	ReadFacebookArticleClient readArticleClient;
	
	private Gson gson;
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "publish_stream", "read_stream");
	
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
		sendRequestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendRequestDialog();
			}
		});
		
		authButton.setFragment(this);// 반두시 필요
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				publishStory();
			}
		});
		
		printHashKey();
	}
	
	@Background
	void login() {
		DefaultRestClient rc = new DefaultRestClient();
		try {
			String result = rc.post(ServerIpAddress.IP + "/login.photo", "fbid", "123123");
			Log.e(TAG, "login result : " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Background
	void checkLogin() {
		DefaultRestClient rc = new DefaultRestClient();
		try {
			String result = rc.get(ServerIpAddress.IP + "/checkreg.photo?fbid=123123");
			Log.e(TAG, "checkLogin result : " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the fragment_facebook_login activity is launched
		// and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
			Log.e("users access token ", session.getAccessToken());
			getPrefsEditor().putString("ACCESS_TOKEN", session.getAccessToken()).apply();
		}
		
		uiHelper.onResume();
	}

	private Editor getPrefsEditor() {
		return getActivity().getSharedPreferences("PHOTIFY", Activity.MODE_PRIVATE).edit();
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
						Log.e(TAG, "Response : " + response);
						Log.e(TAG, "UserID : " + user.getId());
						Log.e(TAG, "User FirstName : " + user.getFirstName());
//						int value = getPreferenceManager().getSharedPreferences().getInt("ACCESS_TOKEN", user.getId());
						getPrefsEditor().putString("FBID", user.getId()).apply();
					}
				}
			});
		}
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
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.e("onSessionStateChange", "state : isOpen?:" + state.isOpened());
		if (state.isOpened()) {
			shareButton.setVisibility(View.VISIBLE);
			if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
				// publishStory();
			}
			sendRequestButton.setVisibility(View.VISIBLE);
		} else if (state.isClosed()) {
			shareButton.setVisibility(View.INVISIBLE);
			sendRequestButton.setVisibility(View.INVISIBLE);
		}
	}
	
	private void publishStory() {
		Session session = Session.getActiveSession();
		if (session != null) {
			
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}
			
			Bundle postParams = new Bundle();
			postParams.putString("name", "Facebook SDK for Android");
			postParams.putString("caption", "Build great social apps and get more installs.");
			postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
			postParams.putString("link", "https://developers.facebook.com/android");
			
			postParams.putString("picture", "https://fbcdn-sphotos-a-a.akamaihd.net/hphotos-ak-prn1/t1/995001_553244524762418_873787853_n.jpg");
			
			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					if (response == null) return;
					Log.e("Request.Callback", "Request.Callback response received!");
					FacebookRequestError error = response.getError();
					if (error != null) Log.e("Request.Callback", error.getErrorType() + " : " + error.getErrorMessage());
					
					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i(TAG, "JSON error " + e.getMessage());
					}
					if (error != null) {
						Toast.makeText(getActivity().getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity().getApplicationContext(), postId, Toast.LENGTH_LONG).show();
					}
				}
			};
			
			Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
			
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) { return false; }
		}
		return true;
	}
	
	private void sendRequestDialog() {
		Bundle params = new Bundle();
		params.putString("message", "Learn how to make your Android apps social");
		
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(getActivity(), Session.getActiveSession(), params)).setOnCompleteListener(
				new WebDialog.OnCompleteListener() {
					
					@Override
					public void onComplete(Bundle values, FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(getActivity().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getActivity().getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
							}
						} else {
							final String requestId = values.getString("request");
							if (requestId != null) {
								Toast.makeText(getActivity().getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getActivity().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
							}
						}
					}
					
				}).build();
		requestsDialog.show();
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
