package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class DetailedActivity extends FragmentActivity implements LocationListener {
	private GoogleMap mmap;
	private LocationManager locationManager;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed);
		
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
		
	}
	
	private void setUpMapIfNeeded() {
		if (mmap == null) {
			mmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2)).getMap();
			if (mmap != null) {
				setUpMap();
			}
		}
	}
	
	private void setUpMap() {
		mmap.setMyLocationEnabled(true);
		mmap.getMyLocation();
	}
	
	boolean locationTag = true;
	
	@Override
	public void onLocationChanged(Location location) {
		if (locationTag) {// 한번만 위치를 가져오기 위해서 tag를 주었습니다
			Log.d("myLog", "onLocationChanged: !!" + "onLocationChanged!!");
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			Toast.makeText(DetailedActivity.this, "위도  : " + lat + " 경도: " + lng, Toast.LENGTH_SHORT).show();
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
