package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailedMapActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnCameraChangeListener, LocationListener {
	
	private GoogleMap mMap;
	private MapView mapView;
	boolean bFirst = true;
	private LocationClient mLocationClient;
	private Button btnSelect;
	
	private final LatLng HAMBURG = new LatLng(33.4961664, 126.536036);
	private final LatLng HAMBURG1 = new LatLng(33.4961664, 126.535036);
	
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_map);
		btnSelect = (Button) findViewById(R.id.btn_select);
		btnSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				// send current marked position
				Intent intent = getIntent();
				intent.putExtra("lat", currentMarkedPosition.latitude);
				intent.putExtra("lng", currentMarkedPosition.longitude);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
		goCurrentLocation(); // 현재위치맵으로 이동(미사용)
		mark();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
			mark();
		}
	}
	
	/*
	 * 현재 위치로 이동시켜준다.
	 */
	public synchronized void goCurrentLocation() {
		/*
		 * Location lcCurrent = mMap.getMyLocation(); if(lcCurrent!=null){
		 * mMap.getProjection().toScreenLocation(new
		 * LatLng(lcCurrent.getLatitude(), lcCurrent.getLongitude())); }
		 */
		// mMap.getProjection().toScreenLocation(new LatLng(36.5, 102.5));
	}
	
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true); // 현재위치를 가져갈 수 있도록 설정
				
			}
		}
	}
	
	private LatLng currentMarkedPosition;
	
	private void mark() {
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		mMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				MarkerOptions markerOptions = new MarkerOptions();
				// markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
				markerOptions.position(latLng);
				
				mMap.clear();
				mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				mMap.addMarker(markerOptions);
				
				currentMarkedPosition = latLng;
			}
		});
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, this);
		}
	}
	
	public void showMyLocation(View view) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			if (bFirst == false) return;
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
			bFirst = false;
		}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
	}
	
	@Override
	public void onDisconnected() {
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}
	
	@Override
	public void onCameraChange(CameraPosition position) {
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
