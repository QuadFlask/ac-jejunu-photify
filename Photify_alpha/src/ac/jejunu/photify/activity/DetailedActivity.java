package ac.jejunu.photify.activity;

import java.net.MalformedURLException;
import java.net.URL;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.ArticleCommand;
import ac.jejunu.photify.entity.FacebookArticle;
import ac.jejunu.photify.entity.FacebookArticle.Comment;
import ac.jejunu.photify.entity.FacebookArticle.User;
import ac.jejunu.photify.rest.ReadArticleClient;
import ac.jejunu.photify.rest.ReadFacebookArticleClient;
import ac.jejunu.photify.view.UrlImageView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EActivity(R.layout.activity_detailed)
public class DetailedActivity extends FragmentActivity implements LocationListener {
	
	@RestService
	ReadArticleClient readArticleClient;
	
	@RestService
	ReadFacebookArticleClient readFacebookArticleClient;
	
	@ViewById(R.id.iv_main_image)
	UrlImageView mainImage;
	@ViewById(R.id.writer_profile)
	LinearLayout writerProfile;
	@ViewById(R.id.user_profile_image)
	UrlImageView userPofile;
	
	@ViewById(R.id.comments_container)
	LinearLayout commentsContainer;
	@ViewById(R.id.tv_user_name)
	TextView tvUserName;
	@ViewById(R.id.et_comment_contents)
	EditText etCommentContents;
	@ViewById(R.id.btn_submit)
	Button btnSubmit;
	
	private GoogleMap map;
	private LocationManager locationManager;
	private String provider;
	
	@AfterViews
	void afterViews() {
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(DetailedActivity.this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);
		
		if (provider == null) { // 위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
			new AlertDialog.Builder(DetailedActivity.this).setTitle("위치서비스 동의").setNeutralButton("이동", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				}
			}).setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			}).show();
		} else { // 위치 정보 설정이 되어 있으면 현재위치를 받아옵니다
			locationManager.requestLocationUpdates(provider, 1, 1, DetailedActivity.this);
			setUpMapIfNeeded();
		}
		
		String postId = getIntent().getStringExtra("POST_ID");
		if (postId == null || postId.length() <= 1) {
			Toast.makeText(this, "error to get post from facebook [" + postId + "]", Toast.LENGTH_SHORT).show();
		} else {
			getArticleFromServer(postId);
		}
	}
	
	private static Gson gson = new Gson();
	
	@Background
	public void getArticleFromServer(String postId) {
		synchronized (this) {
			try {
				ArticleCommand c = gson.fromJson(readArticleClient.readArticle(postId), ArticleCommand.class);
				FacebookArticle fbArticle = gson.fromJson(readFacebookArticleClient.getArticle(postId), FacebookArticle.class);
				
				updateView(c, fbArticle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@UiThread
	void updateView(ArticleCommand c, FacebookArticle fbArticle) {
		try {
			mainImage.setDefaultBackgroundColor(0xff333333 | c.getAvgcolor());
			mainImage.setImageURL(new URL(fbArticle.getImages()[1].getSource()));
			
			UrlImageView writer = (UrlImageView) writerProfile.findViewById(R.id.iv_profilepic);
			writer.setImageURL(new URL(fbArticle.getFrom().getProfileImage()));
			
			TextView name = (TextView) writerProfile.findViewById(R.id.tv_name);
			name.setText(fbArticle.getFrom().getName());
			TextView contents = (TextView) writerProfile.findViewById(R.id.tv_contents);
			contents.setText(fbArticle.getName());
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(c.getPositionAsLatLng(), 10.f));
			
			if (fbArticle.getComments() == null) return;
			Comment[] comments = fbArticle.getComments().getData();
			for (Comment comment : comments) {
				commentsContainer.addView(makeCommentView(comment));
			}
			
			tvUserName.setText("");
			String fbid = getPrefs().getString("FBID", null);
			if (fbid != null) userPofile.setImageURL(new URL(FacebookArticle.resolveProfileImageURL(fbid)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SharedPreferences getPrefs() {
		return getSharedPreferences("PHOTIFY", Activity.MODE_PRIVATE);
	}
	
	private LinearLayout makeCommentView(Comment comment) throws MalformedURLException {
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout commentLayout = (LinearLayout) inflater.inflate(R.layout.user_profile, null);
		
		UrlImageView profile = (UrlImageView) commentLayout.findViewById(R.id.iv_profilepic);
		profile.setImageURL(new URL(comment.getFrom().getProfileImage()));
		
		TextView tvName = (TextView) commentLayout.findViewById(R.id.tv_name);
		tvName.setText(comment.getFrom().getName());
		TextView tvContents = (TextView) commentLayout.findViewById(R.id.tv_contents);
		tvContents.setText(comment.getMessage());
		
		return commentLayout;
	}
	
	private Bitmap getSampledSizeBitmap(String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		Point size = getScreenSize();
		
		opt.inSampleSize = getOptimizedSampleSize(path, Math.max(size.x, size.y));
		return resolveBitmap(path, opt);
	}
	
	private Point getScreenSize() {
		Point size = new Point();
		Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		size.x = d.getWidth();
		size.y = d.getHeight();
		return size;
	}
	
	private Bitmap resolveBitmap(String path, BitmapFactory.Options options) {
		return BitmapFactory.decodeFile(path, options);
	}
	
	private int getOptimizedSampleSize(String fileName, int targetSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(fileName, options);
		return calcRate(targetSize, options);
	}
	
	private int calcRate(int targetSize, BitmapFactory.Options options) {
		int bitmapMaxSize = Math.min(options.outWidth, options.outHeight);
		int result = 1;
		if (targetSize < bitmapMaxSize) result = getBinaryNumberLessThan((int) (((float) bitmapMaxSize) / targetSize));
		return result;
	}
	
	private int getBinaryNumberLessThan(int n) {
		int result;
		for (result = 1; result < 32; result++)
			if (n >> result == 0) break;
		return (int) Math.pow(2, result - 1);
	}
	
	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_detailed)).getMap();
			if (map != null) {
				setUpMap();
			}
		}
	}
	
	private void setUpMap() {
		map.setMyLocationEnabled(true);
		map.getMyLocation();
	}
	
	@Override
	public void onLocationChanged(Location location) {
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}
	
	@Override
	public void onProviderEnabled(String provider) {
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
}
