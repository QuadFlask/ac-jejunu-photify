package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.ArticleCommand;
import ac.jejunu.photify.rest.WriteArticleClient;
import ac.jejunu.photify.rest.WriteArticleClient.OnUploadCompletedCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PhotoInputActivity extends FragmentActivity implements LocationListener {
	private GoogleMap mmap;
	private LocationManager locationManager;
	private String provider;
	public static final int SELECT_IMAGE = 1;
	public static final int RESULT_POSITION_OK = 2;
	private ImageButton imagbtn;
	private Button btnSubmit, mapBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_input);
		
		imagbtn = (ImageButton) findViewById(R.id.imageButton1);
		imagbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGallery();
			}
		});
		
		btnSubmit = (Button) findViewById(R.id.submit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitArticle();
			}
		});
		mapBtn = (Button) findViewById(R.id.mapBtn);
		mapBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PhotoInputActivity.this, DetailedMapActivity.class);
				startActivityForResult(intent, RESULT_POSITION_OK);
			}
		});
		
		article = new ArticleCommand();
		
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(PhotoInputActivity.this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);
		
		if (provider == null) { // 위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
			new AlertDialog.Builder(PhotoInputActivity.this).setTitle("위치서비스 동의").setNeutralButton("이동", new DialogInterface.OnClickListener() {
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
			locationManager.requestLocationUpdates(provider, 1, 1, PhotoInputActivity.this);
			setUpMapIfNeeded();
		}
		mark();
	}
	
	private void mark() {
		mmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		mmap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				MarkerOptions markerOptions = new MarkerOptions();
				// markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
				markerOptions.position(latLng);
				
				mmap.clear();
				mmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				mmap.addMarker(markerOptions);
			}
		});
		mmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	private ArticleCommand article;
	
	private void submitArticle() {
		if (article == null) { return; }
		// TODO
		// show dialog! - starting~!
		final ProgressDialog dialog = ProgressDialog.show(PhotoInputActivity.this, "", "Posting...", true);
		
		String fbid = getPrefs().getString("FBID", null);
		String accessToken = getPrefs().getString("ACCESS_TOKEN", null);
		
		EditText content = (EditText) findViewById(R.id.editText1);
		article.setFbid(fbid);
		article.setContent(content.getText().toString());
		
		Log.e("submitArticle", article.toString());
		
		WriteArticleClient client = new WriteArticleClient();
		client.write(article, accessToken, new OnUploadCompletedCallback() {
			@Override
			public void onUploadCompleted(final String result) {
				dialog.dismiss();
				
				String resultMessage;
				
				if (result != null) resultMessage = "Positing successfully!";
				else resultMessage = "Posting failed!";
				
				AlertDialog.Builder alert = new AlertDialog.Builder(PhotoInputActivity.this);
				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (result != null) finish();
					}
				});
				alert.setMessage(resultMessage);
				alert.show();
			}
		});
	}
	
	private SharedPreferences getPrefs() {
		return getSharedPreferences("PHOTIFY", Activity.MODE_PRIVATE);
	}
	
	private void setUpMapIfNeeded() {
		if (mmap == null) {
			mmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mmap != null) {
				setUpMap();
			}
		}
	}
	
	private void setUpMap() {
		mmap.setMyLocationEnabled(true);
		mmap.getMyLocation();
	}
	
	private void startGallery() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.setType("image/*");
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivityForResult(i, SELECT_IMAGE);
		} catch (android.content.ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case SELECT_IMAGE: {
					Uri uri = intent.getData();
					String path = getPath(uri);
					String name = getName(uri);
					Log.e("###", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : " + uri.toString());
					
					Bitmap sampledSizeBitmap = getSampledSizeBitmap(path);
					imagbtn.setImageBitmap(sampledSizeBitmap);
					
					article.setAttachPath(path);
					break;
				}
				case RESULT_POSITION_OK: {
					Log.e("PhotoInputActivity", "Result : " + intent.getDoubleExtra("lat", 0) + ", " + intent.getDoubleExtra("lng", 0));
					article.setLatLng(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0));
					// TODO set position to marker
				}
			}
		}
	}
	
	private Bitmap getSampledSizeBitmap(String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		
		Point size = new Point();
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		size.set(metrics.widthPixels, metrics.heightPixels);
		
		opt.inSampleSize = getOptimizedSampleSize(path, Math.max(size.x, size.y));
		return resolveBitmap(path, opt);
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
	
	// 실제 경로 찾기
	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	// 파일명 찾기
	private String getName(Uri uri) {
		String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	boolean locationTag = true;
	
	@Override
	public void onLocationChanged(Location location) {
		if (locationTag) {// 한번만 위치를 가져오기 위해서 tag를 주었습니다
			Log.d("myLog", "onLocationChanged: !!" + "onLocationChanged!!");
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			Toast.makeText(PhotoInputActivity.this, "위도  : " + lat + " 경도: " + lng, Toast.LENGTH_SHORT).show();
			locationTag = false;
		}
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
