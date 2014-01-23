package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PhotoInputActivity extends FragmentActivity implements
		LocationListener {
	private GoogleMap mmap;
	private LocationManager locationManager;
	private String provider;
	private final int SELECT_IMAGE = 1;
	private final int SELECT_MOVIE = 2;
	ImageButton imagbtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_input);

		imagbtn = (ImageButton) findViewById(R.id.imageButton1);

		imagbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				startGallery();

			}

		});
		GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(PhotoInputActivity.this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);

		if (provider == null) { // 위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
			new AlertDialog.Builder(PhotoInputActivity.this)
					.setTitle("위치서비스 동의")
					.setNeutralButton("이동",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
											0);
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									finish();
								}
							}).show();
		} else { // 위치 정보 설정이 되어 있으면 현재위치를 받아옵니다
			locationManager.requestLocationUpdates(provider, 1, 1,
					PhotoInputActivity.this);
			setUpMapIfNeeded();
		}
		mark();
	}

	private void mark() {
		mmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		mmap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_launcher));
				markerOptions.position(latLng);

				mmap.clear();
				mmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				mmap.addMarker(markerOptions);

			}
		});
		mmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}

	private void setUpMapIfNeeded() {
		// TODO Auto-generated method stub

		if (mmap == null) {
			mmap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mmap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		// TODO Auto-generated method stub
		mmap.setMyLocationEnabled(true);
		mmap.getMyLocation();

	}

	private void startGallery() {
		// TODO Auto-generated method stub

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
			if (requestCode == SELECT_IMAGE) {
				Uri uri = intent.getData();
				String path = getPath(uri);
				String name = getName(uri);
				String uriId = getUriId(uri);
				Log.e("###", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : "
						+ uri.toString() + "\nuri id : " + uriId);
				imagbtn.setImageURI(uri);
			} else if (requestCode == SELECT_MOVIE) {
				Uri uri = intent.getData();
				String path = getPath(uri);
				String name = getName(uri);
				String uriId = getUriId(uri);
				Log.e("###", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : "
						+ uri.toString() + "\nuri id : " + uriId);
			}
		}
	}

	// 실제 경로 찾기
	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	// 파일명 찾기
	private String getName(Uri uri) {
		String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	// uri 아이디 찾기
	private String getUriId(Uri uri) {
		String[] projection = { MediaStore.Images.ImageColumns._ID };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	boolean locationTag = true;

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (locationTag) {// 한번만 위치를 가져오기 위해서 tag를 주었습니다
			Log.d("myLog", "onLocationChanged: !!" + "onLocationChanged!!");
			double lat = location.getLatitude();
			double lng = location.getLongitude();

			Toast.makeText(PhotoInputActivity.this,
					"위도  : " + lat + " 경도: " + lng, Toast.LENGTH_SHORT).show();
			locationTag = false;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
